/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import java.math.BigDecimal;

/**
 * Item da venda já com o preço e o total calculados pelo backend.
 *
 * @param produto identificador do produto.
 * @param nome nome do produto.
 * @param quantidade quantidade vendida.
 * @param valorUnitario preço de venda no momento da venda.
 * @param valorTotal quantidade multiplicada pelo valor unitário.
 */
public record ItemConsolidado(
    Long produto,
    String nome,
    BigDecimal quantidade,
    BigDecimal valorUnitario,
    BigDecimal valorTotal
) {
}
