/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import com.github.andrepenteado.venda.domain.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Linha da pesquisa de vendas.
 *
 * @param id identificador da venda.
 * @param dataHora data e hora da venda.
 * @param nomeCliente nome do cliente vinculado; nulo quando consumidor.
 * @param cpfCliente CPF do cliente vinculado; nulo quando consumidor.
 * @param formaPagamento forma de pagamento da venda.
 * @param total total da venda.
 * @param valorPago valor líquido pago (total com juros/desconto aplicados).
 */
public record VendaPesquisaResponse(
    Long id,
    LocalDateTime dataHora,
    String nomeCliente,
    Long cpfCliente,
    FormaPagamento formaPagamento,
    BigDecimal total,
    BigDecimal valorPago
) {
}
