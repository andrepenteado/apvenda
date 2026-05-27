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

    private String nome;

    /**
     * Retorna o nome usado na pesquisa de Marca.
     *
     * @return nome usado na pesquisa.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome usado na pesquisa de Marca.
     *
     * @param nome nome usado na pesquisa.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Converte o filtro em predicado QueryDSL.
     *
     * @return predicado QueryDSL da pesquisa.
     */
    public Predicate toPredicate() {
        QMarca marca = QMarca.marca;
        BooleanBuilder builder = new BooleanBuilder();

        if (nome != null && !nome.isBlank()) {
            builder.and(marca.nome.containsIgnoreCase(nome.trim()));
        }

        return builder;
    }

}
