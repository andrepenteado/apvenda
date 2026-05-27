import { Routes } from "@angular/router"
import { AcessoNegadoComponent, ErroProcessamentoComponent, PaginaInicialComponent } from "@andre.penteado/ngx-apcore"

export const DECORATED_ROUTES: Routes = [

  { path: "", component: PaginaInicialComponent },

  {
    path: "marcas",
    loadChildren: () => import("../pages/marca/marca.routes").then(m => m.MARCA_ROUTES)
  },

  {
    path: "categorias",
    loadChildren: () => import("../pages/categoria/categoria.routes").then(m => m.CATEGORIA_ROUTES)
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
