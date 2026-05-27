/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.filter;

import com.github.andrepenteado.venda.domain.entities.QCategoria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

/**
 * Filtro de pesquisa para Categoria.
 */
public class CategoriaFilter {

    private String nome;
    private Boolean ativo;
    private Long categoriaPai;

    /**
     * Retorna o nome usado na pesquisa de Categoria.
     *
     * @return nome usado na pesquisa.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome usado na pesquisa de Categoria.
     *
     * @param nome nome usado na pesquisa.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o filtro de situação ativa.
     *
     * @return situação ativa.
     */
    public Boolean getAtivo() {
        return ativo;
    }

    /**
     * Define o filtro de situação ativa.
     *
     * @param ativo situação ativa.
     */
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * Retorna o identificador da Categoria pai usada na pesquisa.
     *
     * @return identificador da Categoria pai.
     */
    public Long getCategoriaPai() {
        return categoriaPai;
    }

    /**
     * Define o identificador da Categoria pai usada na pesquisa.
     *
     * @param categoriaPai identificador da Categoria pai.
     */
    public void setCategoriaPai(Long categoriaPai) {
        this.categoriaPai = categoriaPai;
    }

    /**
     * Converte o filtro em predicado QueryDSL.
     *
     * @return predicado QueryDSL da pesquisa.
     */
    public Predicate toPredicate() {
        QCategoria categoria = QCategoria.categoria;
        BooleanBuilder builder = new BooleanBuilder();

        if (nome != null && !nome.isBlank()) {
            builder.and(categoria.nome.containsIgnoreCase(nome.trim()));
        }

        if (ativo != null) {
            builder.and(categoria.ativo.eq(ativo));
        }

        if (categoriaPai != null) {
            builder.and(categoria.categoriaPai.id.eq(categoriaPai));
        }

        return builder;
    }

}
