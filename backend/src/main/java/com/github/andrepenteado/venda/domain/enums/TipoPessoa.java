/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 12:26:51 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.enums;

/**
 * Tipo de pessoa do cliente: física (CPF) ou jurídica (CNPJ).
 */
public enum TipoPessoa {

    FISICA("Pessoa Física"),
    JURIDICA("Pessoa Jurídica");

    private final String descricao;

    TipoPessoa(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição apresentável do tipo de pessoa.
     *
     * @return descrição do tipo de pessoa.
     */
    public String getDescricao() {
        return descricao;
    }

}
