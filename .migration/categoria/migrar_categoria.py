#!/usr/bin/env python3
"""
Migração: MAT_006.DBF (grupos do Clipper)  ->  tabela `categoria` (PostgreSQL).

Este script NÃO conecta no banco: ele lê o .DBF e GERA um arquivo .sql pronto
para você rodar em qualquer ambiente (psql, DBeaver, pgAdmin...), sem depender de
driver Python. Leia o MAPEAMENTO.md ao lado antes de rodar — ele documenta cada
decisão de negócio aplicada aqui.

--------------------------------------------------------------------------------
COMO RODAR
--------------------------------------------------------------------------------
Gerar o SQL (padrão: escreve em ../sql/categoria.sql):

    python migrar_categoria.py

Escolher outro arquivo de origem/destino:

    python migrar_categoria.py --dbf /caminho/novo/MAT_006.DBF --output ../sql/categoria.sql

Depois, aplicar o SQL gerado no banco, ex.:

    psql -h localhost -U apvenda-dbuser -d apvenda-dbname -f ../sql/categoria.sql

O SQL é idempotente (UPSERT por id): rodar de novo atualiza em vez de duplicar.
--------------------------------------------------------------------------------
"""

from __future__ import annotations

import argparse
import sys
from collections import Counter
from pathlib import Path

# permite importar o leitor DBF de ../lib
sys.path.insert(0, str(Path(__file__).resolve().parent.parent / "lib"))
from dbf import DBF  # noqa: E402
from texto import normalizar_nome  # noqa: E402

# ------------------------------------------------------------------ constantes
# Caminho padrão do .DBF de origem (relativo à raiz do projeto).
DBF_PADRAO = Path(__file__).resolve().parents[2] / "estoque" / "MAT_006.DBF"
# Arquivo .sql gerado por padrão.
SQL_PADRAO = Path(__file__).resolve().parent.parent / "sql" / "categoria.sql"
# Valor gravado nas colunas de auditoria criado_por / alterado_por.
USUARIO_MIGRACAO = "Migração"


# ------------------------------------------------------------- regras de negócio
def mapear_ativo(bloqueado: str | None) -> bool:
    """
    Deriva `categoria.ativo` a partir do campo BLOQUEADO do Clipper.

    ATENÇÃO (decisão documentada): no MAT_006.DBF de 2012 o campo BLOQUEADO vale
    'S' em 100% dos 160 registros — ou seja, é constante e NÃO distingue grupos
    ativos de inativos. Por isso, na ausência de sinal, migramos todos como
    ATIVOS (true).

    Quando você migrar os dados ATUAIS, confira se aparece 'N' e o que ele
    significa no sistema em uso; se BLOQUEADO='S' passar a significar "inativo",
    troque a regra abaixo por:  return (bloqueado or "").strip().upper() != "S"
    """
    return True


def desambiguar_nomes(registros: list[dict]) -> dict[int, str]:
    """
    O NOMEGRUPO do Clipper tem só 20 caracteres, então nomes longos foram
    truncados e alguns colidem (ex.: 4 grupos viram 'CONJUNTO INTERRUPTOR').
    Como `categoria.nome` é UNIQUE, sufixamos os nomes repetidos com o código
    de origem, ex.: 'CONJUNTO INTERRUPTOR (17)'. Nomes únicos ficam intactos.

    Retorna um dict {codigo: nome_final}. Determinístico (independe da ordem).
    """
    contagem = Counter(r["nome"] for r in registros)
    resultado: dict[int, str] = {}
    for r in registros:
        nome = r["nome"]
        if contagem[nome] > 1:
            nome = f"{nome} ({r['codigo']})"
        resultado[r["codigo"]] = nome
    return resultado


def extrair(caminho_dbf: Path, encoding: str) -> list[dict]:
    """Lê o .DBF e devolve a lista de categorias já transformadas."""
    tabela = DBF(caminho_dbf, encoding=encoding)
    brutos = []
    for reg in tabela.registros():
        codigo = reg.get("CODIGO")
        nome = normalizar_nome((reg.get("NOMEGRUPO") or "").strip())
        if codigo is None or nome == "":
            continue  # linha sem código ou sem nome não vira categoria
        brutos.append({
            "codigo": int(codigo),
            "nome": nome,
            "ativo": mapear_ativo(reg.get("BLOQUEADO")),
        })

    nomes_finais = desambiguar_nomes(brutos)
    categorias = []
    for r in brutos:
        categorias.append({
            "id": r["codigo"],           # preserva o CODIGO do Clipper como id
            "nome": nomes_finais[r["codigo"]],
            "ativo": r["ativo"],
            "categoria_pai": None,        # Clipper é plano: sem hierarquia de grupo
        })
    return categorias


# --------------------------------------------------------------- geração do SQL
def sql_str(valor: str) -> str:
    """Escapa uma string para literal SQL (aspas simples duplicadas)."""
    return "'" + valor.replace("'", "''") + "'"


def gerar_sql(categorias: list[dict], origem: Path) -> str:
    linhas = [
        "-- Migração: MAT_006.DBF (grupos do Clipper) -> categoria",
        f"-- Origem : {origem}",
        f"-- Gerado por: .migration/categoria/migrar_categoria.py",
        f"-- Registros: {len(categorias)}",
        "-- Idempotente: pode ser reaplicado (UPSERT por id).",
        "",
        "BEGIN;",
        "",
    ]
    usuario = sql_str(USUARIO_MIGRACAO)
    for c in categorias:
        pai = "NULL" if c["categoria_pai"] is None else str(c["categoria_pai"])
        linhas.append(
            "INSERT INTO categoria "
            "(id, nome, ativo, fk_categoria_pai, criado_por, criado_em)\n"
            f"VALUES ({c['id']}, {sql_str(c['nome'])}, {str(c['ativo']).lower()}, "
            f"{pai}, {usuario}, CURRENT_TIMESTAMP)\n"
            "ON CONFLICT (id) DO UPDATE SET "
            "nome = EXCLUDED.nome, ativo = EXCLUDED.ativo, "
            f"alterado_por = {usuario}, alterado_em = CURRENT_TIMESTAMP;"
        )
    linhas += [
        "",
        "-- Reposiciona a sequence de id após o maior código, para não colidir",
        "-- com os próximos inserts feitos pela aplicação.",
        "SELECT setval(pg_get_serial_sequence('categoria', 'id'),",
        "              COALESCE((SELECT MAX(id) FROM categoria), 1));",
        "",
        "COMMIT;",
        "",
    ]
    return "\n".join(linhas)


# --------------------------------------------------------------------- CLI/main
def main() -> int:
    p = argparse.ArgumentParser(description="Gera SQL de migração MAT_006.DBF -> categoria")
    p.add_argument("--dbf", type=Path, default=DBF_PADRAO,
                   help=f"caminho do MAT_006.DBF (padrão: {DBF_PADRAO})")
    p.add_argument("--encoding", default="cp850",
                   help="encoding do DBF (padrão cp850 = DOS-BR)")
    p.add_argument("--output", type=Path, default=SQL_PADRAO,
                   help=f"arquivo .sql de saída (padrão: {SQL_PADRAO})")
    p.add_argument("--stdout", action="store_true",
                   help="imprime o SQL na tela em vez de gravar em arquivo")
    args = p.parse_args()

    if not args.dbf.exists():
        print(f"ERRO: arquivo não encontrado: {args.dbf}", file=sys.stderr)
        return 1

    categorias = extrair(args.dbf, args.encoding)
    sql = gerar_sql(categorias, args.dbf)

    if args.stdout:
        print(sql)
    else:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(sql, encoding="utf-8")
        print(f"OK: {len(categorias)} categorias -> {args.output}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
