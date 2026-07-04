/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto.datatables;

import com.github.andrepenteado.venda.domain.filter.VendaFilter;

/**
 * Corpo da consulta server-side do grid de Venda: o request do protocolo
 * DataTables e o filtro da tela de pesquisa.
 *
 * @param datatables request do protocolo DataTables.
 * @param filtro filtro preenchido na tela de pesquisa.
 */
public record VendaDatatablesRequest(DatatablesRequest datatables, VendaFilter filtro) {
}
