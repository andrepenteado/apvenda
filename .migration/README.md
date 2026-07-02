# Migração de dados do sistema legado (Clipper) → PostgreSQL

Este diretório reúne **tudo** que é necessário para migrar os dados do antigo
sistema em Clipper (arquivos `.DBF` na pasta [`estoque/`](../estoque)) para o
banco PostgreSQL do APvenda. Foi escrito para ser entendido e repetido tanto por
**humanos** quanto por **uma IA**, agora e no futuro com dados mais atuais.

> **Para a IA:** ao continuar este trabalho, leia este README inteiro e o
> `MAPEAMENTO.md` da tabela em questão antes de gerar ou alterar scripts.
> Cada script de migração é acompanhado de um `MAPEAMENTO.md` que registra as
> decisões de negócio — respeite-as e atualize-as se algo mudar.

---

## Estrutura da pasta

```
.migration/
├── README.md                 <- este arquivo (visão geral + como repetir)
├── lib/
│   ├── dbf.py                <- leitor genérico de .DBF (stdlib, sem dependências)
│   └── texto.py              <- normalização de nomes (abreviações, acentos, capitalização)
├── sql/                      <- SQLs gerados, prontos para rodar no banco
│   ├── categoria.sql
│   └── produto.sql
├── categoria/                <- 1 pasta por tabela migrada
│   ├── migrar_categoria.py   <- script que LÊ o DBF e GERA o .sql
│   └── MAPEAMENTO.md         <- estrutura de origem, mapeamento e decisões
└── produto/
    ├── migrar_produto.py     <- MAT_004.DBF -> produto (de/para unidade, FKs compat)
    └── MAPEAMENTO.md
```

> **Ordem de aplicação no banco:** `categoria.sql` → (tabela `produto` criada pelo
> CRUD) → `produto.sql`. O `produto.sql` cria os registros de compatibilidade
> (`categoria`/`marca` com nome `Migração`) exigidos pelas FKs obrigatórias.

Cada nova tabela migrada ganha a sua própria subpasta (`produto/`, `cliente/`, …)
com o mesmo par de arquivos: um script `migrar_<tabela>.py` e um `MAPEAMENTO.md`.

---

## Abordagem escolhida (e por quê)

| Decisão | Escolha | Motivo |
|---|---|---|
| **Extração** | Ler o `.DBF` **direto** (binário) | 100% repetível, sem passo manual no LibreOffice, encoding controlado no código. |
| **Linguagem** | **Python** (só biblioteca padrão) | Parsing de DBF é trivial; roda em qualquer máquina **sem instalar nada**. |
| **Destino** | **Gerar arquivo `.sql`** | Sem dependência de driver; o mesmo `.sql` é auditável, versionável e reaproveitável em vários ambientes (psql, DBeaver, pgAdmin...). |

> **Por que não exportar CSV pelo LibreOffice?** Dá para abrir o `.DBF` no Calc e
> salvar CSV, mas isso reintroduz passos manuais (escolher encoding e separador na
> mão) e quebra a repetibilidade — exatamente o que queremos evitar. O leitor
> `lib/dbf.py` lê o binário direto e resolve o encoding sozinho.

### Sobre o encoding (importante)

Os `.DBF` foram gravados em **DOS**, então o texto está em **CP850** (code page
latina do DOS brasileiro), não em UTF-8. O leitor decodifica CP850 → UTF-8
automaticamente (parâmetro `--encoding`, padrão `cp850`). É isso que garante que
acentos e `ç` (ex.: "BRAÇO", "FIXAÇÃO") venham corretos ao migrar os dados atuais.
_(O `MAT_006.DBF` de 2012 por acaso não tem acentos, mas os dados novos terão.)_

---

## Pré-requisitos

- **Python 3.10+** (só stdlib) para gerar o `.sql`. Nada mais precisa ser instalado.
- Para aplicar: qualquer cliente PostgreSQL (`psql`, DBeaver, pgAdmin...). A
  configuração de conexão do projeto está em
  [`backend/src/main/resources/application.yaml`](../backend/src/main/resources/application.yaml)
  (produção) e `application-dev.yaml` (local).

---

## Como rodar uma migração

Duas etapas: **gerar** o `.sql` (Python) e **aplicar** no banco (cliente SQL).
Exemplo com a categoria (ajuste o nome da pasta/script para outras tabelas):

```bash
# 1) Gerar o SQL (escreve em .migration/sql/categoria.sql):
cd .migration/categoria
python migrar_categoria.py

# 2) Aplicar no ambiente desejado:
psql -h localhost -U apvenda-dbuser -d apvenda-dbname -f ../sql/categoria.sql
```

Os SQLs são **idempotentes** (fazem `UPSERT` pela chave primária) e vêm em
`BEGIN/COMMIT`. Rodar de novo **atualiza** os registros existentes em vez de
duplicar.

---

## Como REPETIR no futuro com os dados mais atuais

Este é o objetivo central desta pasta. Quando você conseguir os `.DBF` **atuais**
(fechando o Clipper antes de copiar — veja abaixo), o processo é:

1. Coloque os `.DBF` novos em algum lugar acessível (pode substituir os de
   `estoque/` ou usar outra pasta).
2. Rode o script apontando para o arquivo novo, ex.:
   ```bash
   python migrar_categoria.py --dbf /caminho/novo/MAT_006.DBF
   ```
3. Confira o `.sql` gerado e aplique-o no banco.
4. **Revise as decisões marcadas no `MAPEAMENTO.md`** — algumas dependem dos
   dados. Ex.: o campo `BLOQUEADO` estava constante em 2012; se nos dados novos
   ele variar, a regra de `ativo` precisa ser confirmada (está isolada numa única
   função no script, fácil de ajustar).

> **Lembrete sobre a cópia dos `.DBF` atuais:** o Clipper mantém as tabelas
> abertas em modo exclusivo enquanto o sistema está em uso. Se você compactar/copiar
> com o sistema **aberto**, os arquivos grandes e em uso (ex.: `MAT_004.DBF`) são
> **pulados silenciosamente**. Sempre **feche o sistema Clipper por completo**
> antes de copiar a pasta `estoque/`.

---

## Como adicionar a migração de uma NOVA tabela (guia para humano/IA)

1. Descubra a estrutura do `.DBF` de origem:
   ```bash
   python lib/dbf.py estoque/MAT_XXX.DBF        # imprime campos + amostra
   ```
2. Crie a pasta `.migration/<tabela>/` e copie o padrão de `categoria/` como base.
3. Encontre o alvo no backend:
   - DDL real: `backend/src/main/resources/db/changelog/versions/*.xml`
   - Entidade JPA: `backend/src/main/java/.../domain/entities/<Tabela>.java`
   - Especificação do CRUD: `.cruds/<tabela>.yaml`
   - **Atenção** a colunas `NOT NULL` de auditoria que não aparecem no `.yaml`
     (`criado_por`, `criado_em`) — precisam ser preenchidas na migração.
4. Escreva o `MAPEAMENTO.md` documentando origem → destino e cada decisão.
5. Valide com `--dry-run`, depois aplique.

Se a tabela referenciar códigos de outra (ex.: produto → grupo/categoria),
**preserve o código de origem como `id`** para manter a integridade das FKs entre
as migrações (foi o que fizemos em `categoria`, mantendo `CODIGO` do Clipper como
`categoria.id`).
