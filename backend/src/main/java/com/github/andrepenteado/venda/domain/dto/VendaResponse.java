/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import com.github.andrepenteado.venda.domain.enums.FormaPagamento;

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
 * @param telefoneCliente telefone do cliente vinculado; nulo quando consumidor ou sem telefone.
 * @param itens itens da venda.
 * @param formaPagamento forma de pagamento da venda.
 * @param valorPago valor líquido pago (total com juros/desconto aplicados).
 */
public record VendaResponse(
    Long id,
    LocalDateTime dataHora,
    BigDecimal total,
    String cliente,
    String telefoneCliente,
    List<ItemConsolidado> itens,
    FormaPagamento formaPagamento,
    BigDecimal valorPago
) {
}
