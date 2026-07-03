# APvenda

APvenda é um sistema em desenvolvimento para controle de produtos, marcas e categorias, com evolução prevista para operação de PDV, incluindo abertura e fechamento de caixa e registro de vendas.

Este README registra o contexto específico do produto. Padrões técnicos, stack compartilhada e orientações detalhadas para IA ficam em `.specs/`.

## Visão geral

O objetivo inicial do APvenda é manter uma base organizada de cadastros para apoiar a operação futura de venda presencial.

Nesta fase, o sistema prioriza:

- produtos;
- marcas;
- categorias de produtos.

Esses cadastros devem preparar a base para etapas posteriores de PDV, em que o sistema passará a registrar caixa, vendas, itens vendidos e movimentações financeiras.

## Escopo funcional

### Atual

- Cadastro e manutenção de produtos.
- Cadastro e manutenção de marcas.
- Cadastro e manutenção de categorias.
- Estrutura técnica inicial para evolução dos módulos de PDV.

### Previsto

- Abertura e fechamento de caixa.
- Registro de vendas.
- Controle dos itens vendidos.
- Movimentações de caixa.
- Relatórios operacionais básicos.

## Entidades de referência

Entidades centrais desta fase:

- `Produto`: item comercializado no PDV.
- `Marca`: fabricante, linha ou identificação comercial associada ao produto.
- `Categoria`: classificação usada para organizar produtos.

Entidades previstas para as próximas fases:

- `Caixa`: sessão operacional de caixa.
- `Venda`: registro principal de uma venda realizada.
- `ItemVenda`: item vinculado a uma venda.
- `Receber`: registro financeiro (conta a receber) vinculado a uma venda.
- `MovimentoCaixa`: entrada, saída ou ajuste financeiro do caixa.

## Stack

Stack padrão conforme `.specs/00-contexto-geral.md`:

- Backend: Java 25, Spring Boot 4, PostgreSQL, Liquibase, Logback e QueryDSL.
- Frontend: Angular 21, Node 24, Bootstrap 5, FontAwesome 7, ngx-ui-loader, ngx-toastr, ngx-mask e ng-select.

## Estrutura do projeto

```text
.
├── backend/       # Aplicação backend Spring Boot
├── frontend/      # Aplicação frontend Angular
├── .specs/        # Especificações técnicas e padrões usados por IA
├── .cruds/        # YAMLs operacionais para geração de CRUDs do projeto
├── mise.toml      # Versões de ferramentas usadas no ambiente local
└── README.md      # Contexto específico do APvenda
```

Pontos de entrada principais:

- backend: `backend/src/main/java/com/github/andrepenteado/venda/VendaApplication.java`;
- configurações backend: `backend/src/main/resources/application.yaml` e `backend/src/main/resources/application-dev.yaml`;
- migrações Liquibase: `backend/src/main/resources/db/changelog/`;
- frontend: `frontend/src/main.ts`;
- rotas e configurações frontend: `frontend/src/app/app.routes.ts` e `frontend/src/app/config/`.

## Execução local

Versões esperadas pelo `mise.toml`:

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

O backend usa PostgreSQL. Antes de rodar localmente, revise as configurações de banco, SSO e CORS em `application.yaml` e `application-dev.yaml`.

## Desenvolvimento e IA

Para implementar funcionalidades, manter CRUDs ou orientar assistentes de IA, use `.specs/` como fonte de padrões técnicos.

Arquivos mais relevantes:

- `.specs/00-contexto-geral.md`: stack, regras gerais e critérios globais.
- `.specs/orquestrador.md`: fluxo esperado para geração assistida por IA.
- `.specs/*.md`: regras específicas de backend, frontend, banco, telas e checklist.

YAMLs operacionais de CRUD devem ficar em `.cruds/`, fora de `.specs/`.

## Roadmap

1. Cadastros base: produtos, marcas e categorias.
2. Base de PDV: abertura, fechamento e movimentações de caixa.
3. Vendas: registro de venda, itens, totalizadores e consulta de produtos.
4. Operação: relatórios de vendas, relatórios de caixa e indicadores básicos.

## PDV (Ponto de Venda)

O PDV é a tela de operação de venda presencial. Não é um CRUD: não possui YAML em `.cruds/` e não segue o fluxo de geração assistida. Esta seção define o comportamento esperado da tela e os dados persistidos.

### Objetivo

Registrar uma venda de forma ágil, operada **inteiramente por teclado, sem necessidade de mouse**, com a lista de itens exibida em formato de cupom. Nesta fase o PDV grava apenas a venda e seus itens; o fluxo de caixa (abertura, fechamento e movimentações) ainda não é implementado.

### Fluxo de operação

1. A tela abre com o foco no campo de pesquisa de produto.
2. A pesquisa é um `ng-select` com autocompletar: conforme se digita parte do nome ou o código de barras, abre-se um combo com os produtos que casam com o termo (busca no servidor).
3. As `Setas` navegam pela lista do combo. Selecionar com `Enter` **adiciona o produto direto ao carrinho** (quantidade `1`); selecionar com o **mouse** apenas move o foco para o campo quantidade (com o preço exibido e a quantidade `1`).
4. Com o foco na quantidade, ajusta-se o valor e `Enter` adiciona o produto ao carrinho e devolve o foco para a pesquisa.
5. O campo quantidade fica desabilitado enquanto não houver produto selecionado.
6. O campo preço de venda fica sempre desabilitado (apenas exibição).

### Atalhos de teclado

- `F2`: foco no campo de pesquisa de produto.
- `F3`: foco no campo quantidade, quando habilitado.
- `Del`: entra no modo de navegação dos itens do carrinho; `Seta acima` / `Seta abaixo` selecionam o item e `Enter` confirma a exclusão.
- `F10`: **finalizar venda** (consolida a venda no backend e habilita o formulário de finalização).
- `Ctrl + F11`: confirma a finalização da venda (no modo finalização).
- `Esc`: volta do modo finalização para a montagem da venda (mantém o carrinho).
- `Ctrl + C`: cancela a venda a qualquer momento (o cancelamento exige confirmação, então não copia nem cancela por engano).
- `F4`: imprime o cupom (ver "Impressão do cupom").
- Exclusão de item e cancelamento da venda exibem popup de confirmação; no cancelamento, a opção padrão é **Não** (evita cancelar por engano ao pressionar `Enter`).

No modal de impressão, `[Ctrl + P]` imprime e `[Esc]` fecha.

### Pagamento e finalização

A finalização acontece em um **formulário fixo abaixo da lista de itens** (não há modal de pagamento), com alternância de modo entre montagem e finalização:

- Enquanto se adicionam produtos, os campos do formulário de finalização ficam **readonly**; ao entrar no modo finalização, os campos de produto/quantidade ficam **readonly** e o formulário é habilitado.
- O formulário tem os campos: **Forma de pagamento**, **Juros (%)** e **Desconto (%)** (inteiros, em percentual, aplicados sobre o total) e **Valor pago** (readonly, calculado: `total` + `total` × `juros`% − `total` × `desconto`%).

O fluxo usa duas chamadas ao backend:

1. **Finalizar venda** (`F10`): o frontend envia os itens do carrinho; o backend faz a somatória (totais) e valida, devolvendo o total consolidado. O formulário de finalização é habilitado com o foco na forma de pagamento.
2. **Confirmar** (`Ctrl + F11`, botão do formulário): o frontend envia itens, juros, desconto e forma de pagamento; o backend grava a `Venda`, os `ItemVenda` e **um único `Receber` já quitado** e baixa o estoque. Ao concluir, abre o modal de impressão com o cabeçalho `VENDA #NNN` (id gerado).

O botão `[Esc] Voltar aos itens` retorna à montagem da venda.

### Exibição do carrinho (estilo cupom)

A lista de itens fica abaixo da pesquisa, com aparência de cupom fiscal impresso:

- fundo branco e letras pretas;
- caracteres monoespaçados;
- colunas `PRODUTO` (descrição), `QTD`, `VALOR UNITÁRIO` e `VALOR TOTAL`;
- cabeçalho, itens e rodapé separados por linha tracejada (`--------`);
- rodapé com o total da venda alinhado à direita.

### Impressão do cupom

- `F4` abre um **modal Bootstrap** com o cupom, a qualquer momento da venda; ao finalizar, o modal abre automaticamente. (A tecla `Print` / `PrtSc` é capturada pelo sistema operacional para captura de tela e não é confiável no navegador; por isso `F4`.)
- No modal, `[Ctrl + P]` imprime e `[Esc]` fecha. O `Ctrl + P` usa a impressão nativa do navegador, imprimindo **apenas o conteúdo do modal**: via `@media print`, o restante da tela e os próprios botões do modal ficam ocultos (classe `d-print-none` do Bootstrap).
- Cupom em folha **A4** (`@page { size: A4 }`), com aspecto de cupom fiscal (fundo branco, letras pretas, monoespaçado, separadores tracejados e total à direita).
- O cabeçalho do cupom é **`ORÇAMENTO`** quando impresso antes da finalização e **`VENDA #NNN`** (id da venda) após finalizar.

### Dimensionamento e UX

- No topo à direita da tela, exibir um ícone FontAwesome que lembre um ponto de venda (ex.: `fa-cash-register`), centralizado verticalmente em relação aos campos de produto, quantidade e preço.
- Campos de pesquisa, quantidade e preço usam fonte maior que o padrão, dimensionada para uso em tela cheia num monitor 1024x768 (4:3). O tamanho não deve ser fixo: é apenas referência de cálculo e precisa ficar adequado também em telas widescreen.
- A lista de itens usa fonte menor que a dos campos, porém maior ou igual ao padrão do sistema.
- Priorizar os recursos do Bootstrap 5 no desenho da tela e reduzir ao mínimo o CSS customizado.
- Documentar os atalhos de forma discreta: em um rodapé compacto no PDV e ao lado do label dos campos que tiverem atalho (ex.: `Produto [F2]`, `Quantidade [F3]`).

### Dados e escopo

- As tabelas (sem YAML de CRUD) são `Venda` (`id`, `data_hora`, `total` e auditoria), `ItemVenda` (`id`, `fk_venda`, `fk_produto`, `quantidade`, `valor_unitario`, `valor_total`) e `Receber` (`id`, `fk_venda`, `parcela`, `data_vencimento`, `data_pagamento`, `forma_pagamento`, `valor_a_receber`, `valor_pago`). A modelagem de `Receber` comporta parcelamento, mas a regra atual é simplificada: **1 venda = 1 registro de `Receber`, sempre pago** (`parcela` fixo em 0 = à vista).
- `quantidade` é decimal, para permitir fracionar (ex.: produtos em metro); lançar o mesmo produto de novo soma a quantidade na linha existente.
- `valor_unitario` guarda o preço no momento da venda; o `total` é recalculado no backend a partir dos itens.
- O formulário de finalização informa `juros`, `desconto` (inteiros, em **percentual**) e a `forma_pagamento`. O valor líquido da venda é `total` + `total` × `juros`% − `total` × `desconto`% e vira o único registro de `Receber`, com `valor_a_receber` = `valor_pago` = líquido. Juros e desconto ficam embutidos no valor e não são colunas de `Receber`.
- `data_vencimento` e `data_pagamento` recebem a **data da venda** (registro já quitado).
- `forma_pagamento` é o enum `FormaPagamento` (`DINHEIRO`, `PIX`, `CARTAO_DEBITO`, `CARTAO_CREDITO`).
- Ao finalizar, a venda baixa o `estoque_atual` de cada produto na mesma transação. A baixa fica pronta, mas nesta fase não há validação de estoque negativo ou zero.
- O fluxo de caixa não será implementado nesta fase.

## Tela Vendas

A tela **Vendas** (menu `Vendas`, rota `/vendas`) consulta as vendas registradas no PDV, baseada na tabela `venda`. Não é um CRUD gerado por YAML.

- **Filtros**: número da venda, período (data da venda), CPF do cliente e consumidor (somente vendas sem cliente vinculado). A pesquisa exige ao menos um filtro; limpar os filtros lista todas as vendas.
- **Grid**: a primeira coluna é a de **ação**, com o botão de **estorno**; as demais exibem id, data/hora, cliente (ou "Consumidor"), CPF, forma de pagamento, total e valor pago.
- **Estorno**: mediante confirmação (opção padrão **Não**), exclui a `Venda`, os `ItemVenda` e o `Receber` e **devolve as quantidades vendidas ao estoque**, tudo na mesma transação.
