/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Venda gravada, devolvida ao PDV (usada, por exemplo, na impressão do cupom).
 *
 * @param id identificador da venda.
 * @param dataHora data e hora da venda.
 * @param total total da venda.
 * @param cliente nome do cliente vinculado; nulo quando consumidor.
 * @param itens itens da venda.
 * @param recebimentos parcelas a receber geradas.
 */
public record VendaResponse(
    Long id,
    LocalDateTime dataHora,
    BigDecimal total,
    String cliente,
    List<ItemConsolidado> itens,
    List<ReceberResponse> recebimentos
) {
}
