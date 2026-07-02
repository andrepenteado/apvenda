# Mapeamento: `MAT_006.DBF` (grupos) → tabela `categoria`

Migração dos **grupos** do sistema Clipper para a entidade **Categoria** do
APvenda. Script: [`migrar_categoria.py`](./migrar_categoria.py).

## Origem — `estoque/MAT_006.DBF`

- Formato: dBase III / FoxBase+ (byte de versão `0x03`), sem memo.
- Snapshot usado: **backup de 2012** (atualização interna do DBF: 2011-05-30).
- **160 registros vivos**, 0 marcados como deletados.

| Campo (Clipper) | Tipo | Descrição |
|---|---|---|
| `BLOQUEADO`  | C(1)  | flag de status (ver decisão abaixo) |
| `CODIGO`     | N(3)  | código do grupo (1..163, único) |
| `NOMEGRUPO`  | C(20) | nome do grupo (truncado em 20 chars) |
| `POR_COM`    | N(5,2)| % comissão — **descartado** |
| `PERC_EXTRA` | N(5,2)| % extra — **descartado** |
| `MARGEM`     | N(5,2)| margem — **descartado** |
| `FATURAMES`  | N(12,2)| faturamento do mês — **descartado** |
| `QTDMES`     | N(8,2)| quantidade do mês — **descartado** |

## Destino — tabela `categoria`

DDL: `backend/src/main/resources/db/changelog/versions/1.0.xml` ·
Entidade: `Categoria.java` · Spec: `.cruds/categoria.yaml`.

| Coluna | Tipo | Origem |
|---|---|---|
| `id`               | BIGINT PK autoinc | ← `CODIGO` (preservado) |
| `nome`             | VARCHAR UNIQUE    | ← `NOMEGRUPO` (tratado) |
| `ativo`            | BOOLEAN NOT NULL  | ← derivado de `BLOQUEADO` |
| `fk_categoria_pai` | BIGINT NULL       | `NULL` (Clipper não tem hierarquia) |
| `criado_por`       | VARCHAR NOT NULL  | constante `'Migração'` |
| `criado_em`        | TIMESTAMP NOT NULL| `CURRENT_TIMESTAMP` (na aplicação do SQL) |
| `alterado_por/em`  | —                 | preenchidos só no re-run (UPSERT) |

## Decisões de negócio (importantes)

1. **`id` = `CODIGO` do Clipper (preservado).**
   Os produtos (`MAT_004.DBF`) referenciam o grupo pelo código. Manter
   `categoria.id = CODIGO` deixa a futura migração de produtos ligar direto na
   categoria, sem tabela de/para. Ao final o script roda `setval(...)` para
   reposicionar a sequence de `id` após o maior código, evitando colisão com os
   próximos inserts feitos pela aplicação.

2. **`nome` — colisões por truncamento.**
   `NOMEGRUPO` tem só 20 caracteres; nomes longos foram truncados e **6 registros
   colidem** (o nome completo se perdeu no Clipper):
   - `CONJUNTO INTERRUPTOR` → 4 grupos (códigos 27, 102, 115, 136)
   - `ABRACADEIRAS TIPO D`  → 2 grupos (3, 4)
   - `SOQUETE PARA LAMPADA` → 2 grupos
   - `HIDRAULICA EM GERAL`  → 2 grupos

   Como `categoria.nome` é **UNIQUE**, os nomes repetidos recebem o código de
   origem como sufixo, ex.: `Conjunto interruptor (27)`. Nomes únicos ficam
   intactos. É determinístico (independe da ordem de leitura).

   O `nome` passa pela normalização de `lib/texto.py` (só quando a origem está toda
   em maiúsculo): remove preenchimento de pontos, **desfaz abreviações** (`P/`→para,
   `ABRAC.`→abraçadeira, …), **restaura acentos/cedilha** (`ACO`→aço, `HIDRAULICA`→
   hidráulica, …) e capitaliza só a 1ª letra mantendo letras sozinhas em maiúsculo
   (`ABRACADEIRAS TIPO D` → `Abraçadeiras tipo D`). Os dicionários são extensíveis.

3. **`ativo` — `BLOQUEADO` é constante.**
   No backup de 2012, `BLOQUEADO = 'S'` em **100%** dos 160 registros: não
   distingue ativo de inativo. Na ausência de sinal, todos são migrados como
   **ativos (`true`)**. A regra está isolada na função `mapear_ativo()` do script.
   > ⚠️ Ao migrar os dados **atuais**, verifique se aparece `'N'` e o que ele
   > significa no sistema em uso. Se `'S'` passar a significar "bloqueado/inativo",
   > troque a função por `return (bloqueado or "").strip().upper() != "S"`.

4. **Campos financeiros/estatísticos descartados** (`POR_COM`, `PERC_EXTRA`,
   `MARGEM`, `FATURAMES`, `QTDMES`): não existem no modelo `categoria`. Se um dia
   forem necessários, entram em outra tabela, não aqui.

5. **`fk_categoria_pai` sempre `NULL`:** o cadastro de grupos do Clipper é plano,
   sem grupo-pai. A hierarquia do APvenda pode ser montada manualmente depois.

## Como rodar

O script **gera um arquivo `.sql`** (não conecta no banco), que você aplica no
ambiente que quiser.

```bash
cd .migration/categoria
python migrar_categoria.py                     # gera ../sql/categoria.sql (160 INSERTs)

# aplicar no banco (exemplo com psql):
psql -h localhost -U apvenda-dbuser -d apvenda-dbname -f ../sql/categoria.sql
```

O SQL é idempotente (UPSERT por `id`) e vem envolto em `BEGIN/COMMIT`. Para
regenerar a partir de um DBF mais novo: `--dbf /caminho/novo/MAT_006.DBF`.

## Verificação pós-migração (sugestão)

```sql
SELECT count(*) FROM categoria;                 -- esperado: 160 (a partir deste DBF)
SELECT count(*) <> count(DISTINCT nome) AS tem_dup FROM categoria;  -- deve ser false
SELECT max(id) FROM categoria;                  -- 163; a sequence deve estar > 163
```
