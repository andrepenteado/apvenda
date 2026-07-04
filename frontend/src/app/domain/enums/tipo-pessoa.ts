/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 12:26:51 -03
 * Observação: arquivo criado com ajuda da IA.
 */
export enum TipoPessoa {
  FISICA = 'FISICA',
  JURIDICA = 'JURIDICA'
}

export const TipoPessoaLabels: Record<TipoPessoa, string> = {
  [TipoPessoa.FISICA]: 'Pessoa Física',
  [TipoPessoa.JURIDICA]: 'Pessoa Jurídica'
};
