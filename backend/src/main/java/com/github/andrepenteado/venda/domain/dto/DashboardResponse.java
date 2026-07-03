/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 23:20:00 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import com.github.andrepenteado.venda.domain.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Resumo financeiro consolidado para o dashboard de vendas.
 *
 * @param totalHoje valor total vendido hoje.
 * @param quantidadeHoje quantidade de vendas de hoje.
 * @param totalMes valor total vendido no mês corrente.
 * @param quantidadeMes quantidade de vendas do mês corrente.
 * @param ticketMedioMes valor médio por venda no mês corrente.
 * @param vendasPorDia série diária de vendas dos últimos 30 dias.
 * @param topProdutos produtos mais vendidos do mês corrente.
 * @param formasPagamento distribuição do faturamento do mês por forma de pagamento.
 * @param topClientes clientes que mais compraram no mês corrente.
 */
public record DashboardResponse(
    BigDecimal totalHoje,
    long quantidadeHoje,
    BigDecimal totalMes,
    long quantidadeMes,
    BigDecimal ticketMedioMes,
    List<VendaDia> vendasPorDia,
    List<TopProduto> topProdutos,
    List<FormaPagamentoTotal> formasPagamento,
    List<TopCliente> topClientes
) {

    /**
     * Total vendido em um dia.
     *
     * @param data dia da venda.
     * @param total valor total vendido no dia.
     * @param quantidade quantidade de vendas do dia.
     */
    public record VendaDia(LocalDate data, BigDecimal total, long quantidade) {
    }

    /**
     * Produto mais vendido.
     *
     * @param nome nome do produto.
     * @param quantidade quantidade vendida.
     * @param total valor total vendido.
     */
    public record TopProduto(String nome, BigDecimal quantidade, BigDecimal total) {
    }

    /**
     * Total faturado por forma de pagamento.
     *
     * @param formaPagamento forma de pagamento.
     * @param total valor total faturado.
     */
    public record FormaPagamentoTotal(FormaPagamento formaPagamento, BigDecimal total) {
    }

    /**
     * Cliente que mais comprou.
     *
     * @param nome nome do cliente.
     * @param quantidade quantidade de vendas.
     * @param total valor total comprado.
     */
    public record TopCliente(String nome, long quantidade, BigDecimal total) {
    }

}
