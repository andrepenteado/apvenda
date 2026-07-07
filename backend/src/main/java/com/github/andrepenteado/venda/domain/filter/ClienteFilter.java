/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.filter;

import br.unesp.fc.andrepenteado.core.web.utils.TextoUtils;
import com.github.andrepenteado.venda.domain.entities.QCliente;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

/**
 * Filtro de pesquisa para Cliente.
 */
public class ClienteFilter {

    private String nome;

    private Long cpfCnpj;

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
     * Retorna o CPF/CNPJ usado na pesquisa de Cliente.
     *
     * @return CPF/CNPJ usado na pesquisa.
     */
    public Long getCpfCnpj() {
        return cpfCnpj;
    }

    /**
     * Define o CPF/CNPJ usado na pesquisa de Cliente.
     *
     * @param cpfCnpj CPF/CNPJ usado na pesquisa.
     */
    public void setCpfCnpj(Long cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
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
            // Pesquisa no campo denormalizado, sem considerar acentos nem
            // maiúsculas/minúsculas.
            builder.and(cliente.pesquisa.contains(TextoUtils.normalizar(nome.trim())));
        }

        if (cpfCnpj != null) {
            builder.and(cliente.cpfCnpj.eq(cpfCnpj));
        }

        return builder;
    }

}
