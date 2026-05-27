/*
 * Autor: André Penteado
 * Criado em: 26/05/2026 17:21:01 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.filter;

import com.github.andrepenteado.venda.domain.entities.QMarca;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

/**
 * Filtro de pesquisa para Marca.
 */
public class MarcaFilter {

    private String descricao;

    /**
     * Retorna a descrição usada na pesquisa de Marca.
     *
     * @return descrição usada na pesquisa.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define a descrição usada na pesquisa de Marca.
     *
     * @param descricao descrição usada na pesquisa.
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Converte o filtro em predicado QueryDSL.
     *
     * @return predicado QueryDSL da pesquisa.
     */
    public Predicate toPredicate() {
        QMarca marca = QMarca.marca;
        BooleanBuilder builder = new BooleanBuilder();

        if (descricao != null && !descricao.isBlank()) {
            builder.and(marca.descricao.containsIgnoreCase(descricao.trim()));
        }

        return builder;
    }

}
