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
 * Dados de uma parcela informados no modal de pagamento. O valor a receber é
 * recalculado pelo backend; o {@code valorPago} nulo indica parcela em aberto.
 *
 * @param formaPagamento forma de pagamento da parcela.
 * @param dataVencimento data de vencimento (digitada/editável).
 * @param valorPago valor pago; nulo quando a parcela fica em aberto.
 */
public record ParcelaRequest(
    FormaPagamento formaPagamento,
    LocalDate dataVencimento,
    BigDecimal valorPago
) {
}
