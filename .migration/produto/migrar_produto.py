#!/usr/bin/env python3
"""
Migração: MAT_004.DBF (cadastro de produtos do Clipper) -> tabela `produto` (PostgreSQL).

Gera um arquivo .sql pronto para rodar (não conecta no banco). Leia o
MAPEAMENTO.md ao lado para as decisões de negócio.

ORDEM DE EXECUÇÃO NO BANCO (importante):
  1) A tabela `produto` já deve existir (CRUD gerado a partir de .cruds/produto.yaml).
  2) Rodar antes o .migration/sql/categoria.sql (para as categorias existirem).
  3) Aplicar este .migration/sql/produto.sql.

    python migrar_produto.py            # gera ../sql/produto.sql
    psql -h host -U user -d dbname -f ../sql/produto.sql
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent / "lib"))
from dbf import DBF  # noqa: E402
from texto import normalizar_nome  # noqa: E402

DBF_PADRAO = Path(__file__).resolve().parents[2] / "estoque" / "MAT_004.DBF"
SQL_PADRAO = Path(__file__).resolve().parent.parent / "sql" / "produto.sql"
USUARIO_MIGRACAO = "Migração"

# De/para da unidade: no legado só existem PC e MT; o resto é lixo/typo.
# Como `unidade` é obrigatória, valor não mapeado recebe o default UNIDADE.
DE_PARA_UNIDADE = {"PC": "PECA", "MT": "METRO"}
UNIDADE_DEFAULT = "UNIDADE"

NUM_RE = re.compile(r"^-?\d+(\.\d+)?$")


def sql_str(valor: str) -> str:
    return "'" + valor.replace("'", "''") + "'"


def num_sql(texto: str | None) -> str:
    """Emite o número como literal SQL (preservando os dígitos) ou NULL."""
    if texto is None:
        return "NULL"
    t = str(texto).strip()
    return t if NUM_RE.match(t) else "NULL"


def extrair(caminho_dbf: Path, encoding: str) -> list[dict]:
    tabela = DBF(caminho_dbf, encoding=encoding)
    # o leitor já converte N -> int/float; para números preservamos o texto cru,
    # relendo o campo, então aqui pegamos os valores como vieram e formatamos.
    produtos = []
    for reg in tabela.registros():
        codigo = reg.get("CODIGO")
        nome = normalizar_nome((reg.get("DESCRICAO") or "").strip())
        if codigo is None or nome == "":
            continue
        grupo = reg.get("GRUPO")
        unidade_raw = (reg.get("UNIDADE") or "").strip().upper()
        produtos.append({
            "id": int(codigo),
            "nome": nome,
            "codigo_barras": (reg.get("CODBAR") or "").strip() or None,
            "grupo": int(grupo) if grupo is not None else None,
            "unidade": DE_PARA_UNIDADE.get(unidade_raw, UNIDADE_DEFAULT),
            "preco_venda": reg.get("VENDA"),
            "custo_compra": reg.get("CUSTOCOM"),
            "estoque_atual": reg.get("QTATUAL"),
            "ativo": (reg.get("ATIVO") or "").strip().upper() == "S",
        })
    return produtos


def gerar_sql(produtos: list[dict], origem: Path) -> str:
    L = [
        "-- Migração: MAT_004.DBF (produtos do Clipper) -> produto",
        f"-- Origem : {origem}",
        "-- Gerado por: .migration/produto/migrar_produto.py",
        f"-- Produtos: {len(produtos)}",
        "-- Pré-requisitos: tabela produto criada; categoria.sql já aplicado.",
        "-- Idempotente: reaplicável (UPSERT por id).",
        "",
        "BEGIN;",
        "",
        "-- Registros de compatibilidade para as FKs obrigatórias -------------------",
        "INSERT INTO categoria (nome, ativo, criado_por, criado_em)",
        f"VALUES ({sql_str(USUARIO_MIGRACAO)}, true, {sql_str(USUARIO_MIGRACAO)}, CURRENT_TIMESTAMP)",
        "ON CONFLICT (nome) DO NOTHING;",
        "",
        "INSERT INTO marca (nome, criado_por, criado_em)",
        f"VALUES ({sql_str(USUARIO_MIGRACAO)}, {sql_str(USUARIO_MIGRACAO)}, CURRENT_TIMESTAMP)",
        "ON CONFLICT (nome) DO NOTHING;",
        "",
        "-- Produtos -----------------------------------------------------------------",
        "INSERT INTO produto",
        "    (id, nome, codigo_barras, fk_categoria, fk_marca, unidade, preco_venda,",
        "     custo_compra, estoque_atual, ativo, criado_por, criado_em)",
        "SELECT v.id, v.nome, v.codigo_barras,",
        "       COALESCE(cat.id, compat_cat.id) AS fk_categoria,",
        "       compat_marca.id                 AS fk_marca,",
        "       v.unidade, v.preco_venda, v.custo_compra, v.estoque_atual, v.ativo,",
        f"       {sql_str(USUARIO_MIGRACAO)}, CURRENT_TIMESTAMP",
        "FROM (VALUES",
    ]

    linhas_values = []
    for p in produtos:
        grupo = "NULL" if p["grupo"] is None else str(p["grupo"])
        unidade = "NULL" if p["unidade"] is None else sql_str(p["unidade"])
        cod_barras = "NULL" if p["codigo_barras"] is None else sql_str(p["codigo_barras"])
        linhas_values.append(
            f"    ({p['id']}, {sql_str(p['nome'])}, {cod_barras}, {grupo}, {unidade}, "
            f"{num_sql(p['preco_venda'])}, {num_sql(p['custo_compra'])}, "
            f"{num_sql(p['estoque_atual'])}, {str(p['ativo']).lower()})"
        )
    L.append(",\n".join(linhas_values))

    L += [
        ") AS v(id, nome, codigo_barras, grupo, unidade, preco_venda, custo_compra, estoque_atual, ativo)",
        "LEFT JOIN categoria cat ON cat.id = v.grupo",
        f"CROSS JOIN (SELECT id FROM categoria WHERE nome = {sql_str(USUARIO_MIGRACAO)}) AS compat_cat",
        f"CROSS JOIN (SELECT id FROM marca     WHERE nome = {sql_str(USUARIO_MIGRACAO)}) AS compat_marca",
        "ON CONFLICT (id) DO UPDATE SET",
        "    nome          = EXCLUDED.nome,",
        "    codigo_barras = EXCLUDED.codigo_barras,",
        "    fk_categoria  = EXCLUDED.fk_categoria,",
        "    fk_marca      = EXCLUDED.fk_marca,",
        "    unidade       = EXCLUDED.unidade,",
        "    preco_venda   = EXCLUDED.preco_venda,",
        "    custo_compra  = EXCLUDED.custo_compra,",
        "    estoque_atual = EXCLUDED.estoque_atual,",
        "    ativo         = EXCLUDED.ativo,",
        f"    alterado_por  = {sql_str(USUARIO_MIGRACAO)},",
        "    alterado_em   = CURRENT_TIMESTAMP;",
        "",
        "-- Reposiciona a sequence de id após o maior código.",
        "SELECT setval(pg_get_serial_sequence('produto', 'id'),",
        "              COALESCE((SELECT MAX(id) FROM produto), 1));",
        "",
        "COMMIT;",
        "",
    ]
    return "\n".join(L)


def main() -> int:
    p = argparse.ArgumentParser(description="Gera SQL de migração MAT_004.DBF -> produto")
    p.add_argument("--dbf", type=Path, default=DBF_PADRAO)
    p.add_argument("--encoding", default="cp850")
    p.add_argument("--output", type=Path, default=SQL_PADRAO)
    p.add_argument("--stdout", action="store_true")
    args = p.parse_args()

    if not args.dbf.exists():
        print(f"ERRO: arquivo não encontrado: {args.dbf}", file=sys.stderr)
        return 1

    produtos = extrair(args.dbf, args.encoding)
    sql = gerar_sql(produtos, args.dbf)

    if args.stdout:
        print(sql)
    else:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(sql, encoding="utf-8")
        print(f"OK: {len(produtos)} produtos -> {args.output}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
