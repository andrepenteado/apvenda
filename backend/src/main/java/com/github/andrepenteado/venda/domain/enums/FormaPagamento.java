/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.enums;

/**
 * Formas de pagamento aceitas no PDV. O nome da constante é o valor gravado no banco.
 */
public enum FormaPagamento {

    DINHEIRO("Dinheiro"),
    PIX("PIX"),
    CARTAO_DEBITO("Cartão de débito"),
    CARTAO_CREDITO("Cartão de crédito");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição apresentável da forma de pagamento.
     *
     * @return descrição da forma de pagamento.
     */
    public String getDescricao() {
        return descricao;
    }

}
