# CRUD_TEMPLATE.md

## OBJETIVO

Criar um CRUD novo a partir de um YAML em `.cruds/[nome-crud].yaml`.

Stack alvo:
- Backend: Spring Boot 4, Java 25, PostgreSQL, Liquibase e Logback.
- Frontend: Angular 21, Node 24, Bootstrap 5, FontAwesome 7, ngx-ui-loader, ngx-toastr, ngx-mask e ng-select.

Escopo:
- Criar backend: changelog Liquibase, enum, entidade JPA, repository, service e resource REST.
- Criar frontend: rotas, menu, constante de API, enum, entidade TypeScript, service e componentes de pesquisa/cadastro.
- Não criar testes.
- Não alterar CRUD já gerado. Se o CRUD, manifesto ou arquivos esperados já existirem, rejeite a execução.

---

## COMO EXECUTAR

1. Leia `.cruds/*.yaml`, ignorando `*.generated.yaml`.
2. Valide cada YAML e apresente um relatório com status.
3. Pergunte explicitamente quais CRUDs novos executar.
4. Só altere código após confirmação do usuário.
5. Antes de criar arquivos, inspecione o projeto real e siga seus padrões.
6. Não invente dependências, APIs, caminhos ou estilos se já houver padrão equivalente.
7. Ao final, informe arquivos criados/alterados e qualquer validação executada.

Status:

| Status | Critério | Ação |
|---|---|---|
| `novo` | YAML válido, sem manifesto e sem arquivos esperados existentes. | Pode criar após confirmação. |
| `existente` | Manifesto `.cruds/[nome].generated.yaml` existe. | Rejeitar. |
| `conflito` | Manifesto ausente, mas algum arquivo esperado do CRUD já existe. | Rejeitar e listar conflitos. |
| `invalido` | YAML incompleto ou inconsistente. | Rejeitar e explicar. |

Depois de uma criação bem-sucedida, crie `.cruds/[nome-crud].generated.yaml` apenas como registro da execução:

```yaml
crud: marca
metadados: .cruds/marca.yaml
checksumNormalizado: "sha256:..."
executadoEm: "YYYY-MM-DDTHH:mm:ss-03:00"
status: criado
arquivos:
  - backend/src/main/java/.../Marca.java
  - frontend/src/app/services/marca.service.ts
```

O checksum deve ser calculado a partir do YAML parseado em formato canônico, ignorando comentários, linhas em branco e indentação, mas preservando a ordem das listas.

---

## YAML DO CRUD

```yaml
# .cruds/marca.yaml

crud:
  nome: marca

autor: Nome do Autor

projeto:
  nome: venda
  perfis:
    - VendaApplication.PERFIL_CAIXA:CAIXA

tabela:
  nome: marca
  label: Marca
  plural: Marcas
  campos:
    - nome: id
      tipo: long
      pk: true
      autoincremento: true
      exibe-grid: true

    - nome: descricao
      tipo: string
      label: Descrição
      obrigatorio: true
      unique: true
      indice: true
      pesquisavel: contem
      colunas-layout: 8*
      exibe-grid: true
```

### Referência

```yaml
crud:
  nome: [nome-crud]

autor: [nome_autor]

projeto:
  nome: [nome_projeto]
  perfis:
    - [NomeClasseMain].PERFIL_1:[PERFIL_FRONTEND_1]

tabela:
  nome: [nome_tabela]
  label: [Label Amigável]
  plural: [Label Amigável Plural]
  campos:
    - nome: [nome_campo]
      tipo: string | integer | long | boolean | date | datetime | decimal
      label: [Label do Campo]
      pk: true | false
      autoincremento: true | false
      obrigatorio: true | false
      unique: true | false
      indice: true | false
      fk: [nome_tabela_referenciada]
      fk-display: [nome_campo_exibido_no_combo]
      enum: [NomeEnum]:VALOR_A:Descrição A, VALOR_B:Descrição B
      mask: "000.000.000-00"
      pesquisavel: exato | contem | false
      colunas-layout: 0 | [1-12][*]
      exibe-grid: true | false
```

Metadados obrigatórios: `crud.nome`, `autor`, `projeto.nome`, `projeto.perfis`, `tabela.nome`, `tabela.label`, `tabela.plural` e `tabela.campos`.

Propriedades de campo:

| Propriedade | Default | Efeito |
|---|---|---|
| `nome` | - | Nome snake_case. |
| `tipo` | - | Tipo lógico para SQL, Java, TypeScript e input Angular. |
| `label` | Capitalização de `nome` | Texto de formulário e mensagens. |
| `pk` | `false` | Primary key `pk_[nome_tabela]`. |
| `autoincremento` | `false` | Coluna auto incrementável quando aplicável. |
| `obrigatorio` | `false` | `NOT NULL`, validação backend/frontend e feedback visual. |
| `unique` | `false` | Unique `un_[nometabelasemseparador]_[nomecamposemseparador]`. |
| `indice` | `false` | Índice `idx_[nometabelasemseparador]_[nomecamposemseparador]`. |
| `fk` | - | Relacionamento JPA, FK SQL e `ng-select`. |
| `fk-display` | `label`, `descricao`, `nome` ou `id` existente | Campo exibido no `ng-select`. |
| `enum` | - | Enum Java/TypeScript, check constraint e radio buttons. |
| `mask` | - | Atributo `mask` do ngx-mask. |
| `pesquisavel` | `false` | `exato` usa igualdade; `contem` usa busca parcial case-insensitive e só vale para `string`. |
| `colunas-layout` | - | Largura Bootstrap no cadastro. `0` oculta no formulário; ausente usa `col-12` em row exclusiva. |
| `exibe-grid` | `true` | Exibição na tabela de pesquisa. |

Convenções:

| Forma | Regra | Exemplo |
|---|---|---|
| `[nome_tabela]` | Minúsculo com `_`. | `nota_fiscal` |
| `[nome-tabela]` | Minúsculo com `-`. | `nota-fiscal` |
| `[NomeTabela]` | PascalCase. | `NotaFiscal` |
| `[nometabelasemseparador]` | Minúsculo sem separador. | `notafiscal` |
| `[NOME_TABELA_PLURAL]` | `tabela.plural` em maiúsculo, sem acentos e com `_`. | `NOTAS_FISCAIS` |
| `[nome-tabela-plural]` | `tabela.plural` em minúsculo, sem acentos e com `-`. | `notas-fiscais` |

Pacote base backend: `com.github.andrepenteado.[nome_projeto]`.

---

## REGRAS GERAIS

- Gere CRUD simples, sem DTOs, mappers, interfaces de service ou camadas extras, salvo se o projeto já exigir.
- Use português do Brasil em mensagens, logs, validações e comentários.
- Mensagens devem usar `tabela.label`, não termos genéricos como "Registro".
- Concordância: labels terminados em "a" usam feminino; demais usam masculino. Ex.: `Marca salva`, `Produto salvo`.
- Todo arquivo `.java` ou `.ts` criado deve iniciar com comentário contendo `[autor]`, `[data_hora]` no formato pt_BR e uma breve descrição adicionando a observação de que foi criado com ajuda da IA.
- Toda classe `.java` criado deve usar `javadoc` antes da declaração da classe e de cada método para descrever as suas funcionalidades.
- Preserve código existente e não sobrescreva arquivos não pertencentes ao CRUD.

### Auditoria

Todo CRUD deve incluir:

| Campo Java/TypeScript | Coluna PostgreSQL | Tipo | Preenchimento |
|---|---|---|---|
| `criadoPor` | `criado_por` | `string` / `VARCHAR` | Inclusão |
| `criadoEm` | `criado_em` | `datetime` / `TIMESTAMP` | Inclusão |
| `alteradoPor` | `alterado_por` | `string` / `VARCHAR` | Alteração |
| `alteradoEm` | `alterado_em` | `datetime` / `TIMESTAMP` | Alteração |

Regras:
- Gere esses campos mesmo ausentes no YAML.
- Não exiba auditoria no grid nem como controles editáveis.
- No cadastro, exiba auditoria apenas como texto discreto `opacity-75` abaixo do título/aba: 1 linha com criação e, se houver, 1 linha com alteração.
- O service injeta `SecurityService` da lib própria e obtém o usuário com `securityService.getUserLogin().getLogin()`, sem fallback.
- O service preenche inclusão com usuário logado e horário atual do backend; na alteração preserva criação e preenche alteração.
- Se houver auditoria global no projeto, siga o padrão existente.

### Layout do Cadastro

O formulário usa grid Bootstrap de 12 colunas; no mobile todo campo é `col-12`.

| Valor | Resultado |
|---|---|
| `0` | Não renderiza no formulário. |
| `N` | Campo `col-12 col-md-N`; o próximo entra na mesma row se couber. |
| `N*` | Igual a `N`, fechando a row após o campo. |
| Ausente | Campo `col-12` em row exclusiva. |

Algoritmo:

```text
acumulador = 0
Para cada campo:
  0: não renderiza e não altera a row.
  sem colunas-layout: fecha row aberta, cria row exclusiva col-12 e zera.
  N ou N*: se acumulador + N > 12, fecha row e zera; adiciona campo; fecha row se "*" ou acumulador == 12.
Ao final, fecha row aberta.
```

---

## BACKEND

### 1. Liquibase

- Leia `<version>` de `backend/pom.xml` e use `major.minor`: `1.3.2` vira `1.3`.
- Alvo: `backend/src/main/resources/db/changelog/versions/[major.minor].xml`; se existir, adicione um `<changeSet>` nele.
- `id`: data atual `YYYY-MM-DD`; `author`: `autor`.
- Tipos SQL: `string`=`VARCHAR`, `integer`=`INTEGER`, `long`=`BIGINT`, `boolean`=`BOOLEAN`, `date`=`DATE`, `datetime`=`TIMESTAMP`, `decimal`=`NUMERIC`.
- Gere constraints/índices conforme metadados, usando os nomes definidos na tabela de propriedades.
- Sempre adicione auditoria; `criado_por` e `criado_em` são `NOT NULL`, alteração permite `NULL`.

### 2. Enum Java

Para cada campo `enum`, crie `[pacote-base].domain.enums.[NomeEnum]` com propriedade `descricao` e getter.

### 3. Entidade JPA

Classe: `[pacote-base].domain.entities.[NomeTabela]`.

- `id` deve ser `Long`.
- Não use `@Table`.
- Use `@Column` apenas em campo `unique`.
- `obrigatorio`: `@NotBlank` para `string`, `@NotNull` para demais; mensagem `[label_campo] é um campo obrigatório`.
- `fk`: `@ManyToOne` e `@JoinColumn(name = "fk_[nometabelareferenciadasemseparador]")`.
- `enum`: `@Enumerated(EnumType.STRING)`.
- Inclua auditoria, `Serializable`, `serialVersionUID`, `equals/hashCode` por `id` e `toString()` com todos os campos.

### 4. Repository

Interface: `[pacote-base].domain.repositories.[NomeTabela]Repository`, estendendo `JpaRepository<[NomeTabela], Long>`.

- Use métodos nativos para CRUD trivial.
- Para campos `pesquisavel`, crie método tipado:
  - `exato`: `findBy[NomeCampo](TipoCampo valor)`.
  - `contem`: `findBy[NomeCampo]ContainingIgnoreCase(String valor)`.
  - `fk` exato: preferir `findBy[NomeCampo]Id(Long id)` quando compatível.
- Use `@Query` apenas quando método derivado não atender.

### 5. Service

Classe concreta: `[pacote-base].services.[NomeTabela]Service`.

- Métodos: `listar()`, `buscar(Long id)`, `incluir(@Valid obj)`, `alterar(@Valid obj, Long id)`, `excluir(Long id)`.
- `incluir` exige `id == null` e preenche auditoria de criação.
- `alterar` exige `obj.id == id`, busca registro existente, preserva auditoria de criação e preenche auditoria de alteração.
- Se houver pesquisa, crie `pesquisar(String campo, String valor)` com mapeamento explícito, sem reflexão; aceite apenas campos pesquisáveis e converta `valor` para o tipo correto.
- Rejeite `pesquisavel: contem` em campo não `string`.
- Aplique `@Secured` em todos os métodos usando os perfis antes de `:` em `projeto.perfis`.
- Se a constante de perfil não existir na classe main, crie-a com valor `ROLE_[pacote-base]_[NOME_PERFIL]`.
- Use logs com `tabela.label`.

### 6. Resource REST

Classe: `[pacote-base].resources.[NomeTabela]Resource`.

- Endpoint base: `[nome-tabela-plural]`.
- Use `@Observed` na classe.
- Não receba `@AuthenticationPrincipal`; auditoria é responsabilidade do service via `SecurityService`.
- Retorne objetos diretamente, sem `ResponseEntity`, exceto se o padrão do projeto exigir.
- Logue endpoint, parâmetros e `tabela.label`.

| Verbo | Path | Delegação |
|---|---|---|
| `GET` | `/[nome-tabela-plural]` | `servico.listar()` |
| `GET` | `/[nome-tabela-plural]/pesquisar?campo=&valor=` | `servico.pesquisar(campo, valor)` |
| `GET` | `/[nome-tabela-plural]/{id}` | `servico.buscar(id)` |
| `POST` | `/[nome-tabela-plural]` | `servico.incluir(obj)` |
| `PUT` | `/[nome-tabela-plural]/{id}` | `servico.alterar(obj, id)` |
| `DELETE` | `/[nome-tabela-plural]/{id}` | `servico.excluir(id)` |

Crie `/pesquisar` somente se houver campo pesquisável.

---

## FRONTEND

### Convenções Angular

- Componentes `standalone: true`.
- Use `inject()`, `@if`, `@for` e tipos específicos; nunca `any`.
- Selector: `[nomeprojeto]-[nometabela]-[feature]`.
- Telas em `src/app/pages/[nome-tabela]/pesquisar/` e `src/app/pages/[nome-tabela]/cadastro/`.
- Componentes usam `templateUrl` com HTML separado e `styles: [...]` inline; não crie CSS separado.

### API

- Use `INIT_CONFIG` de `src/app/config/init-config.token.ts`.
- Em `src/app/config/api.ts`, adicione `export const API_[NOME_TABELA_PLURAL]: string = '/[nome-tabela-plural]';`.
- O service Angular concatena diretamente `${this.initConfig.urlBackend}${API_[NOME_TABELA_PLURAL]}` em cada método.
- Não declare `baseUrl`, `resourceUrl`, `/api` hardcoded nem chamada HTTP com caminho relativo puro.

### 1. Rotas

- Crie `src/app/pages/[nome-tabela]/[nome-tabela].routes.ts`.
- Adicione em `src/app/config/routes.ts` a rota `[nome-tabela]` com `loadChildren`.
- Rotas internas: `pesquisar`, `cadastro`, `cadastro/:id`, carregando componentes dos diretórios correspondentes.

### 2. Menu

- Atualize `src/app/config/menu.ts` preservando o padrão.
- Adicione item raiz, sem grupo/submenu.
- Use `id: "[nome-tabela]"`, `texto: "tabela.plural"`, `path: "/[nome-tabela-plural]"`, `icone` coerente ou `"tag"`.
- Roles vêm do trecho depois de `:` em `projeto.perfis`, no formato `` `${PREFIXO_PERFIL_SISTEMA}PERFIL` ``.

### 3. Enum TypeScript

Para cada `enum`, crie `src/app/domain/enums/[nome-enum].ts` com `enum` e `Record<Enum, string>` de labels.

### 4. Entidade TypeScript

Arquivo: `src/app/domain/entities/[nome-tabela].ts`.

- `id?: number`.
- `fk`: tipo da entidade referenciada.
- `enum`: tipo do enum gerado.
- Auditoria opcional: `criadoPor`, `criadoEm`, `alteradoPor`, `alteradoEm`.

### 5. Service Angular

Arquivo: `src/app/services/[nome-tabela].service.ts`.

- Injete `HttpClient` e `INIT_CONFIG` com `inject()`.
- Métodos: `listar()`, `buscar(id)`, `incluir(obj)`, `alterar(obj, id)`, `excluir(id)` e `pesquisar(campo, valor)` quando houver campo pesquisável.
- Para pesquisa, use `HttpParams` e endpoint `/pesquisar`.
- Exporte constante `[NOME_TABELA]_CAMPOS_PESQUISA` com `{ campo, label, tipo }` para reutilização no componente.

### 6. Componente Pesquisar

Diretório: `src/app/pages/[nome-tabela]/pesquisar/`.
Arquivos: `pesquisar.componente.ts` e `pesquisar.componente.html`.

- Selector: `[nomeprojeto]-[nometabela]-pesquisar`.
- Breadcrumb: home `/pagina-inicial` > `tabela.plural`.
- Loader: `NgxUiLoaderService`, `loaderId="[nome-tabela]-pesquisar"`, `startLoader()` antes do HTTP e `stopLoader()` no `finalize()`.
- Encapsular conteúdo em `<div class="container">` Bootstrap para layout responsivo e centralizado
- Se houver pesquisa, exiba combo de campos, input de valor, botão `btn-sm` "Pesquisar" (`fa-magnifying-glass`) e "Limpar" (`fa-eraser`).
- Valor vazio na pesquisa: toastr warning `Informe um valor para pesquisar.`.
- Botão "Novo" (`fa-plus`) acima da tabela, alinhado à direita.
- Tabela em `div.table-responsive`, classes `table table-hover table-striped table-bordered align-middle`.
- A `<table>` deve ter `id="datatables-pesquisar-[nome-tabela-plural]"`.
- Primeira coluna "Ações", centralizada, `width: 90px`; editar navega para cadastro, excluir chama service e recarrega apenas em sucesso.
- Exclusão: confirme com SweetAlert2 e mensagem `Confirma a exclusão [NomeTabela] de ID #[id]`.
- Após `listar()` preencher a lista no `subscribe.next`, inicialize DataTable com `setTimeout(() => $('#datatables-pesquisar-[nome-tabela-plural]').DataTable(Datatables.config), 5)`.
- Colunas: `id` e campos com `exibe-grid: true`; enum usa labels.

### 7. Componente Cadastro

Diretório: `src/app/pages/[nome-tabela]/cadastro/`.
Arquivos: `cadastro.componente.ts` e `cadastro.componente.html`.

- Selector: `[nomeprojeto]-[nometabela]-cadastro`.
- Breadcrumb: home `/pagina-inicial` > `tabela.plural` > Cadastro.
- Loader: `loaderId="[nome-tabela]-cadastro"`; desabilite Salvar enquanto ativo.
- Encapsular conteúdo em `<div class="container">` Bootstrap para layout responsivo e centralizado
- Use formulário reativo em `card shadow-sm` com aba `nav-tabs` "Dados Cadastrais".
- Com `:id`, carregue `service.buscar(id)` e aplique `patchValue`.
- Renderize rows conforme `colunas-layout`; campos `0` não aparecem.
- Auditoria segue a regra geral.
- Campos obrigatórios usam `fw-bold`, `[required]="true"`, `Validators.required` e `invalid-feedback`.
- `mask` vira atributo `mask`.
- `fk` usa `ng-select` com service da entidade referenciada e `fk-display`.
- `enum` usa radio buttons com labels.
- Salvar chama `incluir` ou `alterar`, mostra toastr e volta para `[nome-tabela]/pesquisar` em sucesso.
- HTTP 400/422 mostra warning com mensagem da API; HTTP 500+ mostra erro genérico; ambos permanecem no cadastro.
- Botões finais à direita: Salvar (`fa-floppy-disk`) e Voltar (`fa-arrow-left`).

---

## CHECKLIST FINAL

- Escanear `.cruds/*.yaml`, ignorar `*.generated.yaml` e rejeitar CRUD existente/conflitante.
- Validar metadados obrigatórios, nomes, tipos, FKs, enums, pesquisa e layout.
- Confirmar padrões reais do backend/frontend antes de criar arquivos.
- Gerar backend: changelog, enum, entidade, repository, service e resource.
- Gerar frontend: rotas, menu, API, enum, entidade, service, pesquisa e cadastro.
- Não criar testes.
- Conferir auditoria, perfis, endpoints, selectors, loaderIds, labels, concordância, constraints e nomes derivados.
- Conferir que Angular usa `INIT_CONFIG` + constante de API, sem `baseUrl`, `resourceUrl` ou `/api` hardcoded.
- Criar `.cruds/[nome-crud].generated.yaml` após sucesso.
- Informar arquivos criados/alterados e validações executadas.
