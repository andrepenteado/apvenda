/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { TipoPessoa } from '../enums/tipo-pessoa';

export interface Cliente {

  id?: number;

  nome: string;

  tipoPessoa: TipoPessoa;

  cpfCnpj: number;

  telefone?: string | null;

  whatsapp?: boolean | null;

  observacao?: string | null;

  criadoPor?: string;

  criadoEm?: string;

  alteradoPor?: string;

  alteradoEm?: string;

}
