/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import com.github.andrepenteado.venda.domain.enums.FormaPagamento;

import java.util.List;

/**
 * Dados enviados pelo PDV. Na etapa de preparar, apenas {@code itens} é usado; na
 * finalização, os dados de pagamento também são informados.
 *
 * @param itens itens do carrinho.
 * @param cliente identificador do cliente vinculado; nulo quando consumidor.
 * @param juros juros em percentual inteiro sobre o total (finalização).
 * @param desconto desconto em percentual inteiro sobre o total (finalização).
 * @param formaPagamento forma de pagamento da venda (finalização).
 */
public record VendaRequest(
    List<ItemVendaRequest> itens,
    Long cliente,
    Integer juros,
    Integer desconto,
    FormaPagamento formaPagamento
) {
}
