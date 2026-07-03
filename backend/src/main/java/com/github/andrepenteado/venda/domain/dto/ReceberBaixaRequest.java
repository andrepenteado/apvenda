/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import com.github.andrepenteado.venda.domain.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Dados da baixa em lote de parcelas a receber de uma mesma venda.
 * As parcelas informadas são excluídas e substituídas por um único
 * registro de recebimento com os dados de pagamento.
 *
 * @param idsParcelas identificadores das parcelas em aberto a baixar.
 * @param dataPagamento data do pagamento do recebimento.
 * @param formaPagamento forma de pagamento do recebimento.
 * @param valorPago valor efetivamente pago no recebimento.
 */
public record ReceberBaixaRequest(
    List<Long> idsParcelas,
    LocalDate dataPagamento,
    FormaPagamento formaPagamento,
    BigDecimal valorPago
) {
}
