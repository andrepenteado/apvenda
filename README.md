# APvenda

APvenda e um sistema em desenvolvimento para controle de produtos, marcas e categorias, com evolucao prevista para operacao de PDV, incluindo abertura e fechamento de caixa e registro de vendas.

Este README registra o contexto especifico do produto. Padroes tecnicos, stack compartilhada e orientacoes detalhadas para IA ficam em `.specs/`.

## Visao geral

O objetivo inicial do APvenda e manter uma base organizada de cadastros para apoiar a operacao futura de venda presencial.

Nesta fase, o sistema prioriza:

- produtos;
- marcas;
- categorias de produtos.

Esses cadastros devem preparar a base para etapas posteriores de PDV, em que o sistema passara a registrar caixa, vendas, itens vendidos e movimentacoes financeiras.

## Escopo funcional

### Atual

- Cadastro e manutencao de produtos.
- Cadastro e manutencao de marcas.
- Cadastro e manutencao de categorias.
- Estrutura tecnica inicial para evolucao dos modulos de PDV.

### Previsto

- Abertura e fechamento de caixa.
- Registro de vendas.
- Controle dos itens vendidos.
- Movimentacoes de caixa.
- Relatorios operacionais basicos.

## Entidades de referencia

Entidades centrais desta fase:

- `Produto`: item comercializado no PDV.
- `Marca`: fabricante, linha ou identificacao comercial associada ao produto.
- `Categoria`: classificacao usada para organizar produtos.

Entidades previstas para as proximas fases:

- `Caixa`: sessao operacional de caixa.
- `Venda`: registro principal de uma venda realizada.
- `ItemVenda`: item vinculado a uma venda.
- `MovimentoCaixa`: entrada, saida ou ajuste financeiro do caixa.

## Stack

Stack padrao conforme `.specs/00-contexto-geral.md`:

- Backend: Java 25, Spring Boot 4, PostgreSQL, Liquibase, Logback e QueryDSL.
- Frontend: Angular 21, Node 24, Bootstrap 5, FontAwesome 7, ngx-ui-loader, ngx-toastr, ngx-mask e ng-select.

## Estrutura do projeto

```text
.
├── backend/       # Aplicacao backend Spring Boot
├── frontend/      # Aplicacao frontend Angular
├── .specs/        # Especificacoes tecnicas e padroes usados por IA
├── .cruds/        # YAMLs operacionais para geracao de CRUDs do projeto
├── mise.toml      # Versoes de ferramentas usadas no ambiente local
└── README.md      # Contexto especifico do APvenda
```

Pontos de entrada principais:

- backend: `backend/src/main/java/com/github/andrepenteado/venda/VendaApplication.java`;
- configuracoes backend: `backend/src/main/resources/application.yaml` e `backend/src/main/resources/application-dev.yaml`;
- migracoes Liquibase: `backend/src/main/resources/db/changelog/`;
- frontend: `frontend/src/main.ts`;
- rotas e configuracoes frontend: `frontend/src/app/app.routes.ts` e `frontend/src/app/config/`.

## Execucao local

Versoes esperadas pelo `mise.toml`:

- Java: `corretto-25.0.1.8.1`;
- Maven: `4.0.0-rc-5`;
- Node: `24.13.0`.

Backend:

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm start
```

Build e testes:

```bash
cd backend
mvn test
mvn package
```

```bash
cd frontend
npm run build
npm test
```

O backend usa PostgreSQL. Antes de rodar localmente, revise as configuracoes de banco, SSO e CORS em `application.yaml` e `application-dev.yaml`.

## Desenvolvimento e IA

Para implementar funcionalidades, manter CRUDs ou orientar assistentes de IA, use `.specs/` como fonte de padroes tecnicos.

Arquivos mais relevantes:

- `.specs/00-contexto-geral.md`: stack, regras gerais e criterios globais.
- `.specs/orquestrador.md`: fluxo esperado para geracao assistida por IA.
- `.specs/*.md`: regras especificas de backend, frontend, banco, telas e checklist.

YAMLs operacionais de CRUD devem ficar em `.cruds/`, fora de `.specs/`.

## Roadmap

1. Cadastros base: produtos, marcas e categorias.
2. Base de PDV: abertura, fechamento e movimentacoes de caixa.
3. Vendas: registro de venda, itens, totalizadores e consulta de produtos.
4. Operacao: relatorios de vendas, relatorios de caixa e indicadores basicos.
