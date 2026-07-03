/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { FormaPagamento } from '../enums/forma-pagamento';

export interface Receber {

  id?: number;

  idVenda: number;

  nomeCliente?: string | null;

  cpfCliente?: number | null;

  parcela: number;

  dataVencimento: string;

  dataPagamento?: string | null;

  formaPagamento: FormaPagamento;

  valorAReceber: number;

  valorPago?: number | null;

}
