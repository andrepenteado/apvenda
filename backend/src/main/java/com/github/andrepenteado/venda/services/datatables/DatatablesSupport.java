/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.services.datatables;

import com.github.andrepenteado.venda.domain.dto.datatables.DatatablesRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Suporte ao protocolo server-side processing do DataTables: conversão da
 * requisição em {@link Pageable} com ordenação validada por whitelist.
 */
public final class DatatablesSupport {

    private static final int TAMANHO_PADRAO = 10;
    private static final int TAMANHO_MAXIMO = 100;

    private DatatablesSupport() {
    }

    /**
     * Monta o {@link Pageable} da página solicitada. A ordenação usa a coluna
     * enviada pelo grid ({@code columns[order.column].data}) validada contra a
     * whitelist coluna → propriedade da entidade; colunas fora da whitelist são
     * ignoradas (nunca aplicar string do cliente direto no Sort). Sem ordenação
     * válida, aplica a ordenação padrão — paginar sem ORDER BY pode repetir ou
     * pular registros entre páginas.
     *
     * @param request requisição do DataTables.
     * @param colunasOrdenaveis mapa da coluna do grid para a propriedade da entidade.
     * @param ordemPadrao ordenação aplicada quando o grid não ordena por coluna válida.
     * @return página solicitada, com ordenação validada.
     */
    public static Pageable toPageable(DatatablesRequest request, Map<String, String> colunasOrdenaveis, Sort ordemPadrao) {
        int tamanho = request.length() > 0 ? Math.min(request.length(), TAMANHO_MAXIMO) : TAMANHO_PADRAO;
        int pagina = Math.max(request.start(), 0) / tamanho;

        List<Sort.Order> ordens = new ArrayList<>();
        if (request.order() != null && request.columns() != null) {
            for (DatatablesRequest.Order order : request.order()) {
                if (order.column() < 0 || order.column() >= request.columns().size()) {
                    continue;
                }
                String coluna = request.columns().get(order.column()).data();
                String propriedade = colunasOrdenaveis.get(coluna);
                if (propriedade == null) {
                    continue;
                }
                Sort.Direction direcao = "desc".equalsIgnoreCase(order.dir()) ? Sort.Direction.DESC : Sort.Direction.ASC;
                ordens.add(new Sort.Order(direcao, propriedade));
            }
        }

        return PageRequest.of(pagina, tamanho, ordens.isEmpty() ? ordemPadrao : Sort.by(ordens));
    }

    /**
     * Retorna o termo da busca global, ou {@code null} quando vazio.
     *
     * @param request requisição do DataTables.
     * @return termo da busca global normalizado.
     */
    public static String termoBusca(DatatablesRequest request) {
        if (request.search() == null || request.search().value() == null) {
            return null;
        }
        String termo = request.search().value().trim();
        return termo.isEmpty() ? null : termo;
    }

}
