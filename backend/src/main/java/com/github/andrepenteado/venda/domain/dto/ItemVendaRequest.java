/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import java.math.BigDecimal;

/**
 * Item enviado pelo PDV (produto e quantidade) para finalizar a venda.
 *
 * @param produto identificador do produto.
 * @param quantidade quantidade vendida.
 */
public record ItemVendaRequest(Long produto, BigDecimal quantidade) {
}
