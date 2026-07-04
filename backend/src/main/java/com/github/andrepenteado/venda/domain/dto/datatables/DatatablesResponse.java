/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto.datatables;

import java.util.List;

/**
 * Resposta do protocolo server-side processing do DataTables.
 *
 * @param draw contador devolvido tal como recebido na requisição.
 * @param recordsTotal total de registros sem nenhum filtro.
 * @param recordsFiltered total de registros após os filtros.
 * @param data página de registros.
 */
public record DatatablesResponse<T>(
    int draw,
    long recordsTotal,
    long recordsFiltered,
    List<T> data
) {
}
