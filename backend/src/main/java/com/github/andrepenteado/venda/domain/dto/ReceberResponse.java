/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import com.github.andrepenteado.venda.domain.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parcela a receber devolvida ao PDV após a gravação da venda.
 *
 * @param parcela número da parcela (0 = à vista, 1..N = parcelado).
 * @param dataVencimento data de vencimento da parcela.
 * @param dataPagamento data de pagamento (nula quando em aberto).
 * @param formaPagamento forma de pagamento.
 * @param valorAReceber valor a receber da parcela.
 * @param valorPago valor pago (nulo quando em aberto).
 */
public record ReceberResponse(
    Integer parcela,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    FormaPagamento formaPagamento,
    BigDecimal valorAReceber,
    BigDecimal valorPago
) {
}
