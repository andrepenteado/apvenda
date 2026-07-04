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
    id: "cadastros",
    texto: "Cadastros",
    icone: "folder",
    path: "",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: [
      {
        texto: "Marcas",
        icone: "tag",
        path: "/marcas",
        roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`]
      },
      {
        texto: "Categorias",
        icone: "layer-group",
        path: "/categorias",
        roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`]
      },
      {
        texto: "Produtos",
        icone: "box",
        path: "/produtos",
        roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`]
      },
      {
        texto: "Clientes",
        icone: "user",
        path: "/clientes",
        roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`]
      }
    ]
  },

  {
    id: "venda",
    texto: "Venda",
    icone: "cash-register",
    path: "",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: [
      {
        texto: "PDV",
        icone: "cash-register",
        path: "/pdv",
        roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`]
      },
      {
        texto: "Relatório",
        icone: "file-invoice-dollar",
        path: "/vendas",
        roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`]
      }
    ]
  }

];
