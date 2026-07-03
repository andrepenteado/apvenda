/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
export interface Cliente {

  id?: number;

  nome: string;

  cpf: number;

  telefone?: string | null;

  whatsapp?: boolean | null;

  criadoPor?: string;

  criadoEm?: string;

  alteradoPor?: string;

  alteradoEm?: string;

}
