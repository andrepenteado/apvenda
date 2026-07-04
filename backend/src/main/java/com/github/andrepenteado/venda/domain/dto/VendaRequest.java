/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import com.github.andrepenteado.venda.domain.enums.FormaPagamento;

import java.util.List;

/**
 * Dados enviados pelo PDV para finalizar a venda: itens do carrinho e dados de
 * pagamento.
 *
 * @param itens itens do carrinho.
 * @param cliente identificador do cliente vinculado; nulo quando consumidor.
 * @param juros juros em percentual inteiro sobre o total.
 * @param desconto desconto em percentual inteiro sobre o total.
 * @param formaPagamento forma de pagamento da venda.
 */
public record VendaRequest(
    List<ItemVendaRequest> itens,
    Long cliente,
    Integer juros,
    Integer desconto,
    FormaPagamento formaPagamento
) {
}
