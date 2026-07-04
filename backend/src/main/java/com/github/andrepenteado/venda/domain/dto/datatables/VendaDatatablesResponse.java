/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto.datatables;

import com.github.andrepenteado.venda.domain.dto.VendaPesquisaResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resposta server-side do grid de Venda: além da página e dos contadores do
 * protocolo DataTables, devolve os agregados do resultado filtrado inteiro
 * (não apenas da página) para os cards de resumo da tela.
 *
 * @param draw contador devolvido tal como recebido na requisição.
 * @param recordsTotal total de vendas sem nenhum filtro.
 * @param recordsFiltered total de vendas após os filtros.
 * @param data página de vendas.
 * @param valorTotalGeral soma do total das vendas filtradas.
 * @param valorPagoGeral soma do valor pago das vendas filtradas.
 */
public record VendaDatatablesResponse(
    int draw,
    long recordsTotal,
    long recordsFiltered,
    List<VendaPesquisaResponse> data,
    BigDecimal valorTotalGeral,
    BigDecimal valorPagoGeral
) {
}
