/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
export interface Categoria {

  id?: number;

  nome: string;

  ativo: boolean;

  categoriaPai?: Categoria | null;

  criadoPor?: string;

  criadoEm?: string;

  alteradoPor?: string;

  alteradoEm?: string;

}
