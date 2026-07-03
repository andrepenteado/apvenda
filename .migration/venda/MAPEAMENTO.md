# Mapeamento: `MAT_005.DBF` (itens de venda) → tabelas `venda` / `item_venda` / `receber`

Migração do histórico de vendas do Clipper para o PDV do APvenda.
Script: [`migrar_venda.py`](./migrar_venda.py) · Gera: [`../sql/venda.sql`](../sql/venda.sql).

## Origem

O `MAT_005.DBF` isolado é só o **item** da venda (uma linha por produto). Ele faz
parte de um trio de arquivos do legado ligados por `NUMDOC`:

| Arquivo | Papel | Registros vivos |
|---|---|---|
| `MAT_010.DBF` | cabeçalho da venda/cotação | 4.388 |
| `MAT_005.DBF` | **usado nesta migração** — itens da venda | 27.940 |
| `MAT_009.DBF` | parcela/recebimento (duplicata) | 4.374 |

**Decisão: migrar só a partir do `MAT_005.DBF`**, agrupado por `NUMDOC`, em vez de
cruzar os três arquivos. Motivo: `MAT_005` já é **auto-suficiente e 100%
consistente** — nenhum dos 4.379 documentos tem `CLIENTE`, `DATA` ou `VCTO`
divergente entre suas linhas — e cruzar com `MAT_010`/`MAT_009` introduziria
ruído sem necessidade (14 documentos com total divergente entre `MAT_005` e
`MAT_010`, 10 documentos presentes só em um dos arquivos). Snapshot: backup de
2012, dados de **2003-08-08 a 2013-06-17**.

## Destino

DDL: `backend/.../db/changelog/versions/1.0.xml` · Entidades: `Venda.java`,
`ItemVenda.java`, `Receber.java`.

| Coluna | Origem (`MAT_005`, por `NUMDOC`) | Transformação |
|---|---|---|
| `venda.id` | `NUMDOC` | preservado (PK); `setval` ajusta a sequence no fim — mesmo padrão de `categoria`/`produto` |
| `venda.data_hora` | `DATA` (AAAAMMDD) | vira `AAAA-MM-DD 00:00:00` — **hora fixada em meia-noite** (decisão 3; o DBF não guarda hora) |
| `venda.total` | soma de `VALTOT` dos itens migrados | recalculado a partir dos itens efetivamente migrados (não copiado de um total do legado), para nunca divergir da soma de `item_venda` |
| `venda.fk_cliente` | — | sempre `NULL` neste lote (decisão 1, ver abaixo) |
| `item_venda.fk_produto` | `CODIGO` | igual ao `id` do `produto` (preservado desde a migração de `MAT_004.DBF`) |
| `item_venda.quantidade/valor_unitario/valor_total` | `QUANTIDADE`/`VALUNIT`/`VALTOT` | direto (já reconciliam: `quantidade × valor_unitario = valor_total` em 100% da amostra) |
| `receber.parcela` | — | sempre `1` (`PRAZO='1'` em 100% dos registros = à vista) |
| `receber.data_vencimento` | `VCTO` | direto |
| `receber.data_pagamento` | `DTPAGTO` | direto (sempre igual a `VCTO` — pago no mesmo dia) |
| `receber.forma_pagamento` | — | fixo `DINHEIRO` (decisão 2) |
| `receber.valor_a_receber` / `valor_pago` | `VALTOT` (= `venda.total`) | iguais — 100% das vendas no escopo já estão quitadas (`BAIXADO='S'`, `PAGO='S'`) |

## Decisões de negócio

1. **Escopo: só o cliente genérico "CONSUMIDOR".** `MAT_005.CLIENTE` referencia
   `MAT_001.CODIGO` (cadastro combinado cliente/fornecedor do legado). O código
   `1` = "CONSUMIDOR" (venda de balcão, sem cliente identificado) e responde por
   **27.902 dos 27.939 itens vendidos (99,87%)**. Os outros 9 documentos (37
   itens) têm cliente real, mas **são pessoa jurídica (CNPJ)** ou o "CONSUMIDOR"
   sem documento nenhum — o cadastro atual de `cliente` exige `cpf BIGINT NOT
   NULL UNIQUE`, que não representa nem um nem outro caso. Por decisão do
   usuário, **esses 9 documentos ficam de fora desta migração** e serão tratados
   junto da adequação do cadastro de cliente para CPF/CNPJ (tarefa futura — ver
   `venda.id` de cada um logo abaixo, para retomar sem precisar re-analisar o DBF).

   | NUMDOC | Cód. cliente (`MAT_001`) | Data | Itens | Total |
   |---|---|---|---|---|
   | 256  | 3    | 2011-07-04 | 3 | 32,04 |
   | 310  | 102  | 2011-07-11 | 1 | 14,80 |
   | 495  | 114  | 2011-07-28 | 8 | 252,46 |
   | 891  | 11   | 2003-08-08 | 3 | 18,20 |
   | 1893 | 110  | 2011-12-16 | 3 | 44,50 |
   | 1971 | 14   | 2011-12-26 | 6 | 63,57 |
   | 2562 | 2562 | 2012-02-24 | 1 | 10,00 |
   | 2863 | 225  | 2012-03-27 | 4 | 290,00 |
   | 3839 | 14   | 2012-06-27 | 8 | 72,40 |

   > Quando o cadastro de `cliente` suportar CNPJ, migre `MAT_001.DBF` (campos
   > `RAZAO`, `CGC`, `FIRMA`) e depois rode novamente `migrar_venda.py` trocando
   > `CLIENTE_CONSUMIDOR` por uma lógica que resolva `fk_cliente` para os
   > códigos acima (e para qualquer novo código que apareça num backup mais
   > recente).

2. **`forma_pagamento` fixa em `DINHEIRO`.** O legado não registra a forma de
   pagamento: `BANCO`/`CHEQUE` estão em branco em 100% dos 27.939 itens, e
   `TIPOVENDA`/`PAGTO` só dizem "A VISTA" (não existiam PIX/cartão distintos na
   época). Sem sinal para diferenciar, todas as parcelas migradas gravam
   `DINHEIRO`. Isolado em `FORMA_PAGAMENTO_PADRAO`, fácil de trocar se um
   backup mais novo trouxer outro dado.

3. **`data_hora` com hora fixada em meia-noite.** O tipo `D` do DBF só grava
   `AAAAMMDD`; não há componente de hora em nenhum dos três arquivos do trio.

4. **Campos descartados** (sem coluna correspondente no modelo atual): `SERIE`,
   `VENDEDOR`, `USUARIO`, `HISTORICO`, `NOTAFISCAL`, `DESCRICAO` (redundante com
   `produto.nome`), `ICMS`/`VALICMS`/`REDBASE`, `ACRESCIMO`, `FRETE`,
   `COMISSAO`, `OBS`, `CONVENIO`, `BANCO`/`CHEQUE`, `PEDIDO`, `COM_ATU`, `VIAS`,
   `FLAG`, `DIFVENDA`/`DIFERENCA`. Todos são vazios/constantes/zero em 100% da
   amostra ou não têm onde ir no modelo atual.

5. **Itens com produto órfão são pulados, não a venda inteira.** 6 códigos de
   produto (`772`, `1563`, `1931`, `1937`, `1954`, `2016`) aparecem em vendas
   mas não existem no `produto` migrado de `MAT_004.DBF` (10 itens no total, no
   escopo do consumidor). A venda é migrada normalmente com os demais itens;
   `venda.total` é recalculado só com os itens que entraram, então nunca
   diverge da soma de `item_venda`. Nenhuma venda do escopo ficou vazia por
   causa disso (`docs_vazios_pos_filtro = 0`).

6. **1 registro totalmente em branco** no backup de 2012 (sem `NUMDOC`/`CODIGO`/
   `DATA`) é ignorado — lixo de gravação, mesmo padrão de "linha sem dado real"
   das outras migrações.

## Idempotência (diferente de `categoria`/`produto`)

`venda.id` é preservado do `NUMDOC` e faz UPSERT normal (`ON CONFLICT (id) DO
UPDATE`). Mas `item_venda`/`receber` **não têm chave de negócio própria** (PK
autoincrement, sem coluna que volte ao legado) — reaplicar um UPSERT por PK não
faz sentido porque a PK muda a cada execução. Por isso o script:

1. Insere/atualiza `venda` (UPSERT por `id`);
2. **Apaga** todo `item_venda`/`receber` cujo `fk_venda` aponte para uma venda
   com `criado_por = 'Migração'` (ou seja, só as vendas desta migração — nunca
   toca em vendas criadas pelo PDV);
3. Insere `item_venda`/`receber` do zero para o lote migrado.

Validado rodando o `venda.sql` duas vezes seguidas num Postgres descartável: a
segunda execução apaga exatamente o que a primeira inseriu (`DELETE 4369` /
`DELETE 27892`) e reinsere os mesmos totais — sem duplicar.

## Como rodar

```bash
cd .migration/venda
python migrar_venda.py                                     # gera ../sql/venda.sql

# ordem de aplicação no banco: categoria -> produto -> venda
psql -h host -U apvenda-dbuser -d apvenda-dbname -f ../sql/categoria.sql
psql -h host -U apvenda-dbuser -d apvenda-dbname -f ../sql/produto.sql
psql -h host -U apvenda-dbuser -d apvenda-dbname -f ../sql/venda.sql
```

## Verificação pós-migração (já validado num Postgres 16 descartável)

```sql
SELECT count(*) FROM venda;                                -- 4369
SELECT count(*) FROM item_venda;                            -- 27892
SELECT count(*) FROM receber;                                -- 4369 (1 parcela por venda)
SELECT count(*) FROM venda WHERE fk_cliente IS NOT NULL;    -- 0 (todo o lote é consumidor)
SELECT forma_pagamento, count(*) FROM receber GROUP BY 1;   -- só DINHEIRO
SELECT min(data_hora), max(data_hora) FROM venda;           -- 2003-08-08 .. 2013-06-17
-- total da venda deve sempre bater com a soma dos itens:
SELECT v.id FROM venda v JOIN item_venda i ON i.fk_venda = v.id
GROUP BY v.id HAVING v.total <> round(sum(i.valor_total), 2);  -- 0 linhas
```
