import { Routes } from "@angular/router"
import { AcessoNegadoComponent, ErroProcessamentoComponent, PaginaInicialComponent } from "@andre.penteado/ngx-apcore"

export const DECORATED_ROUTES: Routes = [

  { path: "", component: PaginaInicialComponent },

  {
    path: "dashboard",
    loadChildren: () => import("../pages/dashboard/dashboard.routes").then(m => m.DASHBOARD_ROUTES)
  },

  {
    path: "marcas",
    loadChildren: () => import("../pages/marca/marca.routes").then(m => m.MARCA_ROUTES)
  },

  {
    path: "categorias",
    loadChildren: () => import("../pages/categoria/categoria.routes").then(m => m.CATEGORIA_ROUTES)
  },

  {
    path: "produtos",
    loadChildren: () => import("../pages/produto/produto.routes").then(m => m.PRODUTO_ROUTES)
  },

  {
    path: "pdv",
    loadChildren: () => import("../pages/pdv/pdv.routes").then(m => m.PDV_ROUTES)
  },

  {
    path: "clientes",
    loadChildren: () => import("../pages/cliente/cliente.routes").then(m => m.CLIENTE_ROUTES)
  },

  {
    path: "receber",
    loadChildren: () => import("../pages/receber/receber.routes").then(m => m.RECEBER_ROUTES)
  },

  {
    path: "pagina-inicial",
    component: PaginaInicialComponent
  }

]

export const NO_DECORATED_ROUTES: Routes = [

  { path: "erro-processamento", component: ErroProcessamentoComponent },

  { path: "acesso-negado", component: AcessoNegadoComponent }

]
