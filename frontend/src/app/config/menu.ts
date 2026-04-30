import { Menu } from "@andre.penteado/ngx-apcore";
import { PREFIXO_PERFIL_SISTEMA } from "./layout";

export const menu: Menu[] = [
  {
    id: "dashboard", texto: "Dashboard", icone: "chart-line", path: "/dashboard", roles: [`${PREFIXO_PERFIL_SISTEMA}CAIXA`], subMenus: []
  },
];
