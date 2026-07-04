/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Itens precificados e total da venda, consolidados e validados internamente na finalização.
 *
 * @param itens itens consolidados.
 * @param total soma dos totais dos itens.
 */
public record VendaConsolidada(List<ItemConsolidado> itens, BigDecimal total) {
}
