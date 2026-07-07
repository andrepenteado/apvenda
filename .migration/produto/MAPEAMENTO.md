# Mapeamento: `MAT_004.DBF` (produtos) → tabela `produto`

Migração do cadastro de produtos do Clipper para a entidade **Produto** do APvenda.
Script: [`migrar_produto.py`](./migrar_produto.py) · Gera: [`../sql/produto.sql`](../sql/produto.sql).

## Origem — `estoque/MAT_004.DBF`

- dBase III (`0x03`), sem memo. Snapshot: backup de 2012.
- **2165 registros vivos**; 1 descartado por não ter descrição (código 2179).
- 51 campos no legado; só os com dado real e não-calculados foram mapeados.

## Destino — tabela `produto`

Definida por `.cruds/produto.yaml`. Colunas relevantes: `id`, `nome`,
`fk_categoria`, `fk_marca`, `unidade`, `preco_venda`, `custo_compra`,
`estoque_atual`, `ativo`, `fk_foto`, e auditoria (`criado_por`/`criado_em`...).

| Coluna `produto` | Origem (`MAT_004`) | Transformação |
|---|---|---|
| `id`            | `CODIGO` (N,6)   | preservado (PK); `setval` ajusta a sequence no fim |
| `nome`          | `DESCRICAO` (C,35)| trim; CP850→UTF-8; capitalização (só 1ª letra maiúscula) |
| `codigo_barras` | `CODBAR` (C,13)  | trim; vazio → NULL (todo vazio no backup 2012) |
| `fk_categoria`  | `GRUPO` (N,3)    | id da categoria = código do grupo; **fallback** para a categoria de compatibilidade |
| `fk_marca`      | `MARCA` (N,4)    | legado é sempre `1` (loja) → **todos** apontam para a marca de compatibilidade |
| `unidade`       | `UNIDADE` (C,2)  | de/para do enum `Unidade` (ver abaixo) |
| `preco_venda`   | `VENDA` (N,11,2) | número ou NULL |
| `custo_compra`  | `CUSTOCOM` (N,11,2)| número ou NULL |
| `estoque_atual` | `QTATUAL` (N,11,2)| número ou NULL (único campo calculado migrado, a pedido) |
| `ativo`         | `ATIVO` (C,1)    | `'S'` → true; senão false |
| `fk_foto`       | —                | NULL (não existe no legado) |

## Decisões de negócio

1. **De/para de `unidade`** (campo **obrigatório**). No legado só existem duas
   unidades válidas: `PC` (2006) e `MT` (123). Mapeamento: **`PC` → `PECA`**,
   **`MT` → `METRO`**. Todos os outros valores brutos (`00`, vazio, `SP`, `11`,
   `24`, `01`, `UM`, `PV`) são erro de digitação e recebem o **default `UNIDADE`**
   (35 registros). Enum definido em `backend/.../domain/enums/Unidade.java` e
   `frontend/.../domain/enums/unidade.ts`.

2. **Registros de compatibilidade para as FKs obrigatórias** (`categoria` e `marca`
   são `NOT NULL`). O script cria, com `ON CONFLICT (nome) DO NOTHING`:
   - uma **categoria** `nome = 'Migração'` (usada quando o `GRUPO` do produto não
     existir na tabela `categoria`);
   - uma **marca** `nome = 'Migração'` (usada por **todos** os produtos, já que a
     marca do legado é sempre a própria loja e não é uma marca real).

   O vínculo é resolvido em SQL: `fk_categoria = COALESCE(categoria com id=GRUPO,
   categoria 'Migração')` via `LEFT JOIN`; `fk_marca = marca 'Migração'`.

3. **Normalização do `nome`** (`lib/texto.py`, só quando a origem está toda em
   maiúsculo; caixa mista é preservada). Aplica, nesta ordem:
   - remove o preenchimento de pontos (`AMP...............` → `AMP`);
   - **desfaz abreviações** conhecidas (`P/`→para, `C/`→com, `S/`→sem, `PARAF.`→
     parafuso, `ELETR.`→elétrico, `ABRAC.`→abraçadeira, `TELEF.`→telefone, …);
   - **restaura acentos/cedilha** de palavras conhecidas (`ACO`→aço, `FLEXIVEL`→
     flexível, `LAMPADA`→lâmpada, …);
   - capitaliza só a 1ª letra, mantendo letras sozinhas em maiúsculo (`TIPO U`).

   Ex.: `ABRAC. XT-2 P/TELEF.ACO INOX.......` → `Abraçadeira xt-2 para telefone aço
   inox`. Os dicionários (`ABREVIACOES`/`ACENTOS`) são **extensíveis** e devem ser
   revisados/ampliados conforme surgirem novos termos nos dados atuais.

4. **`criado_por` = `'Migração'`** e `criado_em = CURRENT_TIMESTAMP`.

4b. **Campo denormalizado `pesquisa`** (nome + código de barras, sem acentos, em
   minúsculas). Nas gravações pela aplicação o JPA mantém o campo
   (`Produto.atualizarPesquisa()`); como a migração insere via SQL, o script
   emite ao final um `UPDATE produto SET pesquisa = lower(unaccent(...))`
   usando a extensão `unaccent` (criada pelo changelog do Liquibase). A
   expressão deve **espelhar `TextoUtils.normalizar()`** do apcore web.

5. **Campos descartados** (vazios/constantes/calculados): `CODBAR`, `CODIGOSUBS`,
   `DV`, `QTMINIMA`, `QTMAXIMA`, `PEDIDOS`, `CUSTOMED`, `CUSTOIND`, `ICMS`,
   `REDBASE`, `HISTORICO`, `MEDIAMES`, `VENDAMES`, `COMPRA*`, `VENDEU`, `FATUROU`,
   `PR_PORC`, `DESCMARCA`/`FAVORECIDO` (desnormalizados), `ETI1..ETI4`, `APLICACAO`,
   e as datas derivadas `DT*`. (`QTATUAL` é a exceção: foi migrado a pedido.)

## Ordem de execução no banco (importante)

1. A tabela `produto` deve **já existir** (CRUD gerado a partir do YAML).
2. Aplicar antes o `../sql/categoria.sql` (para as categorias reais existirem).
3. Aplicar o `../sql/produto.sql`.

```bash
python migrar_produto.py                                  # regenera o SQL
psql -h host -U apvenda-dbuser -d apvenda-dbname -f ../sql/produto.sql
```

## Verificação pós-migração (sugestão)

```sql
SELECT count(*) FROM produto;                             -- ~2164
SELECT unidade, count(*) FROM produto GROUP BY unidade;   -- PECA/METRO/NULL
SELECT count(*) FROM produto p                            -- quantos caíram na
  JOIN categoria c ON c.id = p.fk_categoria               -- categoria 'Migração'
  WHERE c.nome = 'Migração';
SELECT max(id) FROM produto;                              -- 2178; sequence deve ficar > 2178
```
