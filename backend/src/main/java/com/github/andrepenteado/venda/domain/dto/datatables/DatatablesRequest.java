/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.dto.datatables;

import java.util.List;

/**
 * Requisição do protocolo server-side processing do DataTables: página
 * solicitada (start/length), busca global, ordenação e colunas.
 *
 * @param draw contador de sincronização de requisições do DataTables.
 * @param start índice do primeiro registro da página (offset).
 * @param length quantidade de registros por página.
 * @param search busca global digitada no grid.
 * @param order ordenações aplicadas (índice de coluna e direção).
 * @param columns colunas do grid (a propriedade fica em {@code data}).
 */
public record DatatablesRequest(
    int draw,
    int start,
    int length,
    Search search,
    List<Order> order,
    List<Column> columns
) {

    /**
     * Busca aplicada globalmente ou por coluna.
     *
     * @param value termo digitado.
     * @param regex se o termo deve ser tratado como expressão regular.
     */
    public record Search(String value, boolean regex) {
    }

    /**
     * Ordenação enviada pelo DataTables.
     *
     * @param column índice da coluna no grid.
     * @param dir direção ({@code asc} ou {@code desc}).
     */
    public record Order(int column, String dir) {
    }

    /**
     * Definição de coluna enviada pelo DataTables.
     *
     * @param data propriedade do registro exibida na coluna.
     * @param name nome opcional da coluna.
     * @param searchable se a coluna participa da busca.
     * @param orderable se a coluna é ordenável.
     * @param search busca específica da coluna.
     */
    public record Column(String data, String name, boolean searchable, boolean orderable, Search search) {
    }

}
