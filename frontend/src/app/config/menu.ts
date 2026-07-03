import { Menu } from "@andre.penteado/ngx-apcore";
import { PREFIXO_PERFIL_SISTEMA } from "./layout";

export const menu: Menu[] = [

  {
    id: "dashboard",
    texto: "Dashboard",
    icone: "chart-line",
    path: "/dashboard",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: [
    ]
  },

  {
    id: "marca",
    texto: "Marcas",
    icone: "tag",
    path: "/marcas",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: [
    ]
  },

  {
    id: "categoria",
    texto: "Categorias",
    icone: "layer-group",
    path: "/categorias",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: [
    ]
  },

  {
    id: "produto",
    texto: "Produtos",
    icone: "box",
    path: "/produtos",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: [
    ]
  },

  {
    id: "cliente",
    texto: "Clientes",
    icone: "user",
    path: "/clientes",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: [
    ]
  },

  {
    id: "pdv",
    texto: "PDV",
    icone: "cash-register",
    path: "/pdv",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: [
    ]
  }

];
