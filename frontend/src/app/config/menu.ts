import { Menu } from "@andre.penteado/ngx-apcore";
import { PREFIXO_PERFIL_SISTEMA } from "./layout";

export const menu: Menu[] = [

  {
    id: "marca",
    texto: "Marcas",
    icone: "tag",
    path: "/marcas",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: []
  },

  {
    id: "categoria",
    texto: "Categorias",
    icone: "layer-group",
    path: "/categorias",
    roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`],
    subMenus: []
  }

];
