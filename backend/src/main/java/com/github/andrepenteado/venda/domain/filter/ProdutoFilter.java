/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.filter;

import br.unesp.fc.andrepenteado.core.web.utils.TextoUtils;
import com.github.andrepenteado.venda.domain.entities.QProduto;
import com.github.andrepenteado.venda.domain.enums.Unidade;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

/**
 * Filtro de pesquisa para Produto.
 */
public class ProdutoFilter {

    private String nome;
    private String codigoBarras;
    private Long categoria;
    private Long marca;
    private Unidade unidade;
    private Boolean ativo;

    /**
     * Retorna o nome usado na pesquisa de Produto.
     *
     * @return nome usado na pesquisa.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome usado na pesquisa de Produto.
     *
     * @param nome nome usado na pesquisa.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o código de barras usado na pesquisa.
     *
     * @return código de barras usado na pesquisa.
     */
    public String getCodigoBarras() {
        return codigoBarras;
    }

    /**
     * Define o código de barras usado na pesquisa.
     *
     * @param codigoBarras código de barras usado na pesquisa.
     */
    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    /**
     * Retorna o identificador da Categoria usada na pesquisa.
     *
     * @return identificador da Categoria.
     */
    public Long getCategoria() {
        return categoria;
    }

    /**
     * Define o identificador da Categoria usada na pesquisa.
     *
     * @param categoria identificador da Categoria.
     */
    public void setCategoria(Long categoria) {
        this.categoria = categoria;
    }

    /**
     * Retorna o identificador da Marca usada na pesquisa.
     *
     * @return identificador da Marca.
     */
    public Long getMarca() {
        return marca;
    }

    /**
     * Define o identificador da Marca usada na pesquisa.
     *
     * @param marca identificador da Marca.
     */
    public void setMarca(Long marca) {
        this.marca = marca;
    }

    /**
     * Retorna a unidade usada na pesquisa.
     *
     * @return unidade usada na pesquisa.
     */
    public Unidade getUnidade() {
        return unidade;
    }

    /**
     * Define a unidade usada na pesquisa.
     *
     * @param unidade unidade usada na pesquisa.
     */
    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
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
     * Converte o filtro em predicado QueryDSL.
     *
     * @return predicado QueryDSL da pesquisa.
     */
    public Predicate toPredicate() {
        QProduto produto = QProduto.produto;
        BooleanBuilder builder = new BooleanBuilder();

        if (nome != null && !nome.isBlank()) {
            // Pesquisa no campo denormalizado (nome e código de barras), sem
            // considerar acentos nem maiúsculas/minúsculas.
            builder.and(produto.pesquisa.contains(TextoUtils.normalizar(nome.trim())));
        }

        if (codigoBarras != null && !codigoBarras.isBlank()) {
            builder.and(produto.codigoBarras.eq(codigoBarras.trim()));
        }

        if (categoria != null) {
            builder.and(produto.categoria.id.eq(categoria));
        }

        if (marca != null) {
            builder.and(produto.marca.id.eq(marca));
        }

        if (unidade != null) {
            builder.and(produto.unidade.eq(unidade));
        }

        if (ativo != null) {
            builder.and(produto.ativo.eq(ativo));
        }

        return builder;
    }

}
