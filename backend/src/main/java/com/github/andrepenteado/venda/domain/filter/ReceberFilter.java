/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.filter;

import com.github.andrepenteado.venda.domain.entities.QReceber;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtro de pesquisa para Receber (parcelas a receber das vendas).
 */
public class ReceberFilter {

    private Long idVenda;

    private String nomeCliente;

    private Long cpfCliente;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataFinal;

    private Boolean emAberto;

    /**
     * Retorna o identificador da venda usado na pesquisa de Receber.
     *
     * @return identificador da venda usado na pesquisa.
     */
    public Long getIdVenda() {
        return idVenda;
    }

    /**
     * Define o identificador da venda usado na pesquisa de Receber.
     *
     * @param idVenda identificador da venda usado na pesquisa.
     */
    public void setIdVenda(Long idVenda) {
        this.idVenda = idVenda;
    }

    /**
     * Retorna o nome do cliente usado na pesquisa de Receber.
     *
     * @return nome do cliente usado na pesquisa.
     */
    public String getNomeCliente() {
        return nomeCliente;
    }

    /**
     * Define o nome do cliente usado na pesquisa de Receber.
     *
     * @param nomeCliente nome do cliente usado na pesquisa.
     */
    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    /**
     * Retorna o CPF do cliente usado na pesquisa de Receber.
     *
     * @return CPF do cliente usado na pesquisa.
     */
    public Long getCpfCliente() {
        return cpfCliente;
    }

    /**
     * Define o CPF do cliente usado na pesquisa de Receber.
     *
     * @param cpfCliente CPF do cliente usado na pesquisa.
     */
    public void setCpfCliente(Long cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    /**
     * Retorna a data inicial de vencimento usada na pesquisa de Receber.
     *
     * @return data inicial de vencimento.
     */
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    /**
     * Define a data inicial de vencimento usada na pesquisa de Receber.
     *
     * @param dataInicio data inicial de vencimento.
     */
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * Retorna a data final de vencimento usada na pesquisa de Receber.
     *
     * @return data final de vencimento.
     */
    public LocalDate getDataFinal() {
        return dataFinal;
    }

    /**
     * Define a data final de vencimento usada na pesquisa de Receber.
     *
     * @param dataFinal data final de vencimento.
     */
    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }

    /**
     * Retorna o indicador de parcelas em aberto usado na pesquisa de Receber.
     *
     * @return indicador de parcelas em aberto (sem valor pago).
     */
    public Boolean getEmAberto() {
        return emAberto;
    }

    /**
     * Define o indicador de parcelas em aberto usado na pesquisa de Receber.
     *
     * @param emAberto indicador de parcelas em aberto (sem valor pago).
     */
    public void setEmAberto(Boolean emAberto) {
        this.emAberto = emAberto;
    }

    /**
     * Converte o filtro em predicado QueryDSL.
     *
     * @return predicado QueryDSL da pesquisa.
     */
    public Predicate toPredicate() {
        QReceber receber = QReceber.receber;
        BooleanBuilder builder = new BooleanBuilder();

        if (idVenda != null) {
            builder.and(receber.venda.id.eq(idVenda));
        }

        if (nomeCliente != null && !nomeCliente.isBlank()) {
            builder.and(receber.venda.cliente.nome.containsIgnoreCase(nomeCliente.trim()));
        }

        if (cpfCliente != null) {
            builder.and(receber.venda.cliente.cpf.eq(cpfCliente));
        }

        if (dataInicio != null) {
            builder.and(receber.dataVencimento.goe(dataInicio));
        }

        if (dataFinal != null) {
            builder.and(receber.dataVencimento.loe(dataFinal));
        }

        if (Boolean.TRUE.equals(emAberto)) {
            builder.and(receber.valorPago.isNull());
        }

        return builder;
    }

}
