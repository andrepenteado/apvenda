/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto.datatables;

import com.github.andrepenteado.venda.domain.filter.ClienteFilter;

/**
 * Corpo da consulta server-side do grid de Cliente: o request do protocolo
 * DataTables e o filtro da tela de pesquisa.
 *
 * @param datatables request do protocolo DataTables.
 * @param filtro filtro preenchido na tela de pesquisa.
 */
public record ClienteDatatablesRequest(DatatablesRequest datatables, ClienteFilter filtro) {
}
