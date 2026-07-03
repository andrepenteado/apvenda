/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import com.github.andrepenteado.venda.domain.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parcela a receber devolvida na listagem/pesquisa de Contas a Receber,
 * com os dados do cliente da venda para exibição no grid.
 *
 * @param id identificador da parcela.
 * @param idVenda identificador da venda de origem.
 * @param nomeCliente nome do cliente da venda (nulo quando venda de consumidor).
 * @param cpfCliente CPF do cliente da venda (nulo quando venda de consumidor).
 * @param parcela número da parcela (0 = à vista, 1..N = parcelado).
 * @param dataVencimento data de vencimento da parcela.
 * @param dataPagamento data de pagamento (nula quando em aberto).
 * @param formaPagamento forma de pagamento.
 * @param valorAReceber valor a receber da parcela.
 * @param valorPago valor pago (nulo quando em aberto).
 */
public record ReceberPesquisaResponse(
    Long id,
    Long idVenda,
    String nomeCliente,
    Long cpfCliente,
    Integer parcela,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    FormaPagamento formaPagamento,
    BigDecimal valorAReceber,
    BigDecimal valorPago
) {
}
