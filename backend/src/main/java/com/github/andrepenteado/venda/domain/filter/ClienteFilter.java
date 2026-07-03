/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.filter;

import com.github.andrepenteado.venda.domain.entities.QCliente;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

/**
 * Filtro de pesquisa para Cliente.
 */
public class ClienteFilter {

    private String nome;

    private Long cpf;

    /**
     * Retorna o nome usado na pesquisa de Cliente.
     *
     * @return nome usado na pesquisa.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome usado na pesquisa de Cliente.
     *
     * @param nome nome usado na pesquisa.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o CPF usado na pesquisa de Cliente.
     *
     * @return CPF usado na pesquisa.
     */
    public Long getCpf() {
        return cpf;
    }

    /**
     * Define o CPF usado na pesquisa de Cliente.
     *
     * @param cpf CPF usado na pesquisa.
     */
    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    /**
     * Converte o filtro em predicado QueryDSL.
     *
     * @return predicado QueryDSL da pesquisa.
     */
    public Predicate toPredicate() {
        QCliente cliente = QCliente.cliente;
        BooleanBuilder builder = new BooleanBuilder();

        if (nome != null && !nome.isBlank()) {
            builder.and(cliente.nome.containsIgnoreCase(nome.trim()));
        }

        if (cpf != null) {
            builder.and(cliente.cpf.eq(cpf));
        }

        return builder;
    }

}
