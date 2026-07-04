/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.filter;

import com.github.andrepenteado.venda.domain.entities.QVenda;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Filtro de pesquisa para Venda.
 */
public class VendaFilter {

    private Long idVenda;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataFinal;

    private Long cpfCliente;

    private Boolean consumidor;

    /**
     * Retorna o identificador da venda usado na pesquisa.
     *
     * @return identificador da venda usado na pesquisa.
     */
    public Long getIdVenda() {
        return idVenda;
    }

    /**
     * Define o identificador da venda usado na pesquisa.
     *
     * @param idVenda identificador da venda usado na pesquisa.
     */
    public void setIdVenda(Long idVenda) {
        this.idVenda = idVenda;
    }

    /**
     * Retorna a data inicial da venda usada na pesquisa.
     *
     * @return data inicial da venda.
     */
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    /**
     * Define a data inicial da venda usada na pesquisa.
     *
     * @param dataInicio data inicial da venda.
     */
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * Retorna a data final da venda usada na pesquisa.
     *
     * @return data final da venda.
     */
    public LocalDate getDataFinal() {
        return dataFinal;
    }

    /**
     * Define a data final da venda usada na pesquisa.
     *
     * @param dataFinal data final da venda.
     */
    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }

    /**
     * Retorna o CPF do cliente usado na pesquisa.
     *
     * @return CPF do cliente usado na pesquisa.
     */
    public Long getCpfCliente() {
        return cpfCliente;
    }

    /**
     * Define o CPF do cliente usado na pesquisa.
     *
     * @param cpfCliente CPF do cliente usado na pesquisa.
     */
    public void setCpfCliente(Long cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    /**
     * Retorna o indicador de venda de consumidor (sem cliente vinculado).
     *
     * @return indicador de venda de consumidor.
     */
    public Boolean getConsumidor() {
        return consumidor;
    }

    /**
     * Define o indicador de venda de consumidor (sem cliente vinculado).
     *
     * @param consumidor indicador de venda de consumidor.
     */
    public void setConsumidor(Boolean consumidor) {
        this.consumidor = consumidor;
    }

    /**
     * Converte o filtro em predicado QueryDSL.
     *
     * @return predicado QueryDSL da pesquisa.
     */
    public Predicate toPredicate() {
        QVenda venda = QVenda.venda;
        BooleanBuilder builder = new BooleanBuilder();

        if (idVenda != null) {
            builder.and(venda.id.eq(idVenda));
        }

        if (dataInicio != null) {
            builder.and(venda.dataHora.goe(dataInicio.atStartOfDay()));
        }

        if (dataFinal != null) {
            builder.and(venda.dataHora.lt(dataFinal.plusDays(1).atStartOfDay()));
        }

        if (cpfCliente != null) {
            builder.and(venda.cliente.cpfCnpj.eq(cpfCliente));
        }

        if (Boolean.TRUE.equals(consumidor)) {
            builder.and(venda.cliente.isNull());
        }

        return builder;
    }

}
