/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import java.util.List;

/**
 * Dados enviados pelo PDV. Na etapa de preparar, apenas {@code itens} é usado; na
 * finalização, os dados de pagamento também são informados.
 *
 * @param itens itens do carrinho.
 * @param juros juros em percentual inteiro sobre o total (finalização).
 * @param desconto desconto em percentual inteiro sobre o total (finalização).
 * @param parcelas parcelas informadas (1 registro = à vista; N registros = parcelado).
 */
public record VendaRequest(
    List<ItemVendaRequest> itens,
    Integer juros,
    Integer desconto,
    List<ParcelaRequest> parcelas
) {
}
