import { Routes } from "@angular/router"
import { AcessoNegadoComponent, ErroProcessamentoComponent, PaginaInicialComponent } from "@andre.penteado/ngx-apcore"

export const DECORATED_ROUTES: Routes = [

  { path: "", component: PaginaInicialComponent },

  {
    path: "pagina-inicial",
    component: PaginaInicialComponent
  },

  {
    path: "marca",
    loadChildren: () => import("../pages/marca/marca.routes").then(m => m.MARCA_ROUTES)
  },

  {
    path: "marcas",
    redirectTo: "marca/pesquisar",
    pathMatch: "full"
  }

]

export const NO_DECORATED_ROUTES: Routes = [

  { path: "erro-processamento", component: ErroProcessamentoComponent },

  { path: "acesso-negado", component: AcessoNegadoComponent }

]
