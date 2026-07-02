export enum FormaPagamento {
  DINHEIRO = 'DINHEIRO',
  PIX = 'PIX',
  CARTAO_DEBITO = 'CARTAO_DEBITO',
  CARTAO_CREDITO = 'CARTAO_CREDITO'
}

export const FormaPagamentoLabels: Record<FormaPagamento, string> = {
  [FormaPagamento.DINHEIRO]: 'Dinheiro',
  [FormaPagamento.PIX]: 'PIX',
  [FormaPagamento.CARTAO_DEBITO]: 'Cartão de débito',
  [FormaPagamento.CARTAO_CREDITO]: 'Cartão de crédito'
};
