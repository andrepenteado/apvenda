#!/usr/bin/env python3
"""
Migração: MAT_005.DBF (itens de venda do Clipper) -> tabelas `venda`, `item_venda`
e `receber` (PostgreSQL).

Este script NÃO conecta no banco: ele lê o .DBF e GERA um arquivo .sql pronto
para você rodar em qualquer ambiente (psql, DBeaver, pgAdmin...). Leia o
MAPEAMENTO.md ao lado antes de rodar — ele documenta cada decisão de negócio
aplicada aqui.

ESCOPO DESTA MIGRAÇÃO (leia o MAPEAMENTO.md para o motivo)
--------------------------------------------------------------------------------
Migra só as vendas do cliente genérico "CONSUMIDOR" (código 1 no MAT_001/MAT_005,
~99,8% dos itens de venda do legado). Vendas com cliente identificado (pessoa
jurídica/física real) ficam de fora até o cadastro de `cliente` suportar CNPJ —
ver a lista completa dessas vendas adiadas no MAPEAMENTO.md.

--------------------------------------------------------------------------------
COMO RODAR
--------------------------------------------------------------------------------
Gerar o SQL (padrão: escreve em ../sql/venda.sql):

    python migrar_venda.py

Depois, aplicar o SQL gerado no banco, ex.:

    psql -h localhost -U apvenda-dbuser -d apvenda-dbname -f ../sql/venda.sql

Pré-requisito: as tabelas `venda`/`item_venda`/`receber` já devem existir e o
`produto.sql` já deve ter sido aplicado (item_venda.fk_produto referencia produto.id).

O SQL é idempotente: `venda` é UPSERT por id (preservado do NUMDOC do legado);
`item_venda`/`receber` não têm chave de negócio própria, então o script apaga e
recria os filhos de cada venda migrada antes de reinserir.
--------------------------------------------------------------------------------
"""

from __future__ import annotations

import argparse
import sys
from collections import defaultdict
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent / "lib"))
from dbf import DBF  # noqa: E402

# ------------------------------------------------------------------ constantes
DBF_PADRAO = Path(__file__).resolve().parents[2] / "estoque" / "MAT_005.DBF"
PRODUTO_DBF_PADRAO = Path(__file__).resolve().parents[2] / "estoque" / "MAT_004.DBF"
SQL_PADRAO = Path(__file__).resolve().parent.parent / "sql" / "venda.sql"
USUARIO_MIGRACAO = "Migração"

# Código do cliente genérico "CONSUMIDOR" no legado (MAT_001.CODIGO / MAT_005.CLIENTE).
# Só vendas desse cliente entram nesta migração (ver MAPEAMENTO.md, decisão 1).
CLIENTE_CONSUMIDOR = "1"

# O legado não registra COMO a venda foi paga (BANCO/CHEQUE sempre em branco no
# backup de 2012); todas as vendas no escopo são à vista e já quitadas no mesmo
# dia. Decisão de negócio: gravar todas como DINHEIRO (ver MAPEAMENTO.md).
FORMA_PAGAMENTO_PADRAO = "DINHEIRO"


# ------------------------------------------------------------- regras de negócio
def data_iso(aaaammdd: str | None) -> str | None:
    """Converte a data do Clipper (AAAAMMDD) para o formato ISO (AAAA-MM-DD)."""
    if not aaaammdd or len(aaaammdd) != 8:
        return None
    return f"{aaaammdd[0:4]}-{aaaammdd[4:6]}-{aaaammdd[6:8]}"


def produtos_validos(caminho_dbf: Path, encoding: str) -> set[int]:
    """
    Códigos de produto que existem em produto (mesmo filtro do migrar_produto.py:
    precisa ter CODIGO e DESCRICAO). Usado para não referenciar fk_produto inexistente.
    """
    tabela = DBF(caminho_dbf, encoding=encoding)
    validos = set()
    for reg in tabela.registros():
        codigo = reg.get("CODIGO")
        nome = (reg.get("DESCRICAO") or "").strip()
        if codigo is not None and nome != "":
            validos.add(int(codigo))
    return validos


def extrair(caminho_dbf: Path, encoding: str, produtos: set[int]) -> tuple[list[dict], dict]:
    """Lê o MAT_005.DBF, agrupa por NUMDOC (= 1 venda) e aplica o escopo/filtros."""
    tabela = DBF(caminho_dbf, encoding=encoding)
    por_doc: dict[str, list[dict]] = defaultdict(list)
    for reg in tabela.registros():
        numdoc = reg.get("NUMDOC")
        codigo = reg.get("CODIGO")
        data = reg.get("DATA")
        if numdoc is None or codigo is None or not data:
            continue  # linha lixo (1 registro totalmente em branco no backup de 2012)
        por_doc[str(int(numdoc))].append(reg)

    vendas: list[dict] = []
    stats = {
        "docs_totais": len(por_doc),
        "docs_cliente_real_adiados": 0,
        "itens_cliente_real_adiados": 0,
        "itens_produto_orfao": 0,
        "docs_vazios_pos_filtro": 0,
    }

    for numdoc, linhas in por_doc.items():
        cliente = str(int(linhas[0]["CLIENTE"]))
        if cliente != CLIENTE_CONSUMIDOR:
            stats["docs_cliente_real_adiados"] += 1
            stats["itens_cliente_real_adiados"] += len(linhas)
            continue

        itens = []
        for r in linhas:
            codigo_produto = int(r["CODIGO"])
            if codigo_produto not in produtos:
                stats["itens_produto_orfao"] += 1
                continue
            itens.append({
                "fk_produto": codigo_produto,
                "quantidade": float(r["QUANTIDADE"]),
                "valor_unitario": float(r["VALUNIT"]),
                "valor_total": float(r["VALTOT"]),
            })
        if not itens:
            stats["docs_vazios_pos_filtro"] += 1
            continue

        total = round(sum(i["valor_total"] for i in itens), 2)
        data = data_iso(linhas[0]["DATA"])
        vencimento = data_iso(linhas[0]["VCTO"]) or data
        pagamento = data_iso(linhas[0]["DTPAGTO"]) or vencimento

        vendas.append({
            "id": int(numdoc),
            "data_hora": f"{data} 00:00:00",  # DBF só grava data; hora fixada em meia-noite
            "total": total,
            "itens": itens,
            "vencimento": vencimento,
            "pagamento": pagamento,
        })

    return vendas, stats


# --------------------------------------------------------------- geração do SQL
def sql_str(valor: str) -> str:
    return "'" + valor.replace("'", "''") + "'"


def gerar_sql(vendas: list[dict], origem: Path) -> str:
    usuario = sql_str(USUARIO_MIGRACAO)
    total_itens = sum(len(v["itens"]) for v in vendas)

    linhas = [
        "-- Migração: MAT_005.DBF (itens de venda do Clipper) -> venda / item_venda / receber",
        f"-- Origem : {origem}",
        "-- Gerado por: .migration/venda/migrar_venda.py",
        f"-- Vendas: {len(vendas)}  ·  Itens: {total_itens}",
        "-- Escopo: só vendas do cliente genérico CONSUMIDOR (ver MAPEAMENTO.md).",
        "-- Idempotente: venda é UPSERT por id; item_venda/receber são recriados",
        "-- (apagados e reinseridos) para cada venda migrada por este script.",
        "-- Pré-requisitos: tabelas venda/item_venda/receber criadas; produto.sql já aplicado.",
        "",
        "BEGIN;",
        "",
        "-- Vendas (id preservado do NUMDOC do legado; fk_cliente sempre NULL neste",
        "-- escopo, pois são todas do consumidor genérico) -----------------------------",
        "INSERT INTO venda (id, data_hora, total, fk_cliente, criado_por, criado_em)",
        "VALUES",
    ]

    linhas_venda = []
    for v in vendas:
        linhas_venda.append(
            f"    ({v['id']}, {sql_str(v['data_hora'])}, {v['total']:.2f}, NULL, {usuario}, CURRENT_TIMESTAMP)"
        )
    linhas.append(",\n".join(linhas_venda) + "\nON CONFLICT (id) DO UPDATE SET")
    linhas += [
        "    data_hora    = EXCLUDED.data_hora,",
        "    total        = EXCLUDED.total,",
        "    fk_cliente   = EXCLUDED.fk_cliente,",
        f"    alterado_por = {usuario},",
        "    alterado_em  = CURRENT_TIMESTAMP;",
        "",
        "-- Remove itens/parcelas de uma execução anterior desta mesma migração,",
        "-- antes de reinserir (item_venda/receber não têm chave de negócio própria).",
        f"DELETE FROM receber    WHERE fk_venda IN (SELECT id FROM venda WHERE criado_por = {usuario});",
        f"DELETE FROM item_venda WHERE fk_venda IN (SELECT id FROM venda WHERE criado_por = {usuario});",
        "",
        "-- Itens de venda -------------------------------------------------------------",
        "INSERT INTO item_venda (fk_venda, fk_produto, quantidade, valor_unitario, valor_total)",
        "VALUES",
    ]

    linhas_item = []
    for v in vendas:
        for i in v["itens"]:
            linhas_item.append(
                f"    ({v['id']}, {i['fk_produto']}, {i['quantidade']:.2f}, "
                f"{i['valor_unitario']:.2f}, {i['valor_total']:.2f})"
            )
    linhas.append(",\n".join(linhas_item) + ";")

    linhas += [
        "",
        "-- Contas a receber (1 parcela por venda: legado é 100% à vista e já quitado",
        "-- no mesmo dia; forma de pagamento não é registrada no legado, ver MAPEAMENTO.md) --",
        "INSERT INTO receber (fk_venda, parcela, data_vencimento, data_pagamento,",
        "                     forma_pagamento, valor_a_receber, valor_pago)",
        "VALUES",
    ]

    linhas_receber = []
    for v in vendas:
        linhas_receber.append(
            f"    ({v['id']}, 1, {sql_str(v['vencimento'])}, {sql_str(v['pagamento'])}, "
            f"{sql_str(FORMA_PAGAMENTO_PADRAO)}, {v['total']:.2f}, {v['total']:.2f})"
        )
    linhas.append(",\n".join(linhas_receber) + ";")

    linhas += [
        "",
        "-- Reposiciona a sequence de id após o maior NUMDOC, para não colidir com",
        "-- os próximos inserts feitos pela aplicação.",
        "SELECT setval(pg_get_serial_sequence('venda', 'id'),",
        "              COALESCE((SELECT MAX(id) FROM venda), 1));",
        "",
        "COMMIT;",
        "",
    ]
    return "\n".join(linhas)


# --------------------------------------------------------------------- CLI/main
def main() -> int:
    p = argparse.ArgumentParser(description="Gera SQL de migração MAT_005.DBF -> venda/item_venda/receber")
    p.add_argument("--dbf", type=Path, default=DBF_PADRAO,
                    help=f"caminho do MAT_005.DBF (padrão: {DBF_PADRAO})")
    p.add_argument("--produto-dbf", type=Path, default=PRODUTO_DBF_PADRAO,
                    help=f"caminho do MAT_004.DBF, usado só para validar fk_produto (padrão: {PRODUTO_DBF_PADRAO})")
    p.add_argument("--encoding", default="cp850", help="encoding do DBF (padrão cp850 = DOS-BR)")
    p.add_argument("--output", type=Path, default=SQL_PADRAO, help=f"arquivo .sql de saída (padrão: {SQL_PADRAO})")
    p.add_argument("--stdout", action="store_true", help="imprime o SQL na tela em vez de gravar em arquivo")
    args = p.parse_args()

    if not args.dbf.exists():
        print(f"ERRO: arquivo não encontrado: {args.dbf}", file=sys.stderr)
        return 1
    if not args.produto_dbf.exists():
        print(f"ERRO: arquivo não encontrado: {args.produto_dbf}", file=sys.stderr)
        return 1

    produtos = produtos_validos(args.produto_dbf, args.encoding)
    vendas, stats = extrair(args.dbf, args.encoding, produtos)
    sql = gerar_sql(vendas, args.dbf)

    if args.stdout:
        print(sql)
    else:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(sql, encoding="utf-8")

    total_itens = sum(len(v["itens"]) for v in vendas)
    print(f"OK: {len(vendas)} vendas, {total_itens} itens -> {args.output if not args.stdout else '(stdout)'}",
          file=sys.stderr)
    print(f"    docs de cliente real adiados : {stats['docs_cliente_real_adiados']} "
          f"({stats['itens_cliente_real_adiados']} itens)", file=sys.stderr)
    print(f"    itens de produto órfão pulados: {stats['itens_produto_orfao']}", file=sys.stderr)
    print(f"    docs descartados (sem itens após filtro de produto): {stats['docs_vazios_pos_filtro']}",
          file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
