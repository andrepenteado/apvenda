/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { Unidade } from '../enums/unidade';
import { Categoria } from './categoria';
import { Marca } from './marca';

export interface Produto {

  id?: number;

  nome: string;

  codigoBarras?: string | null;

  categoria: Categoria;

  marca: Marca;

  unidade: Unidade;

  precoVenda?: number | null;

  custoCompra?: number | null;

  estoqueAtual?: number | null;

  ativo: boolean;

  foto?: string | null;

  observacao?: string | null;

  criadoPor?: string;

  criadoEm?: string;

  alteradoPor?: string;

  alteradoEm?: string;

}
