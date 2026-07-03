/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.repositories;

import com.github.andrepenteado.venda.domain.entities.Venda;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório de Venda.
 */
public interface VendaRepository extends JpaRepository<Venda, Long> {

    /**
     * Conta as vendas realizadas em um período.
     *
     * @param inicio data/hora inicial (inclusive).
     * @param fim data/hora final (exclusive).
     * @return quantidade de vendas do período.
     */
    long countByDataHoraGreaterThanEqualAndDataHoraLessThan(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Soma o total das vendas realizadas em um período.
     *
     * @param inicio data/hora inicial (inclusive).
     * @param fim data/hora final (exclusive).
     * @return valor total vendido no período.
     */
    @Query("select coalesce(sum(v.total), 0) from Venda v where v.dataHora >= :inicio and v.dataHora < :fim")
    BigDecimal somarTotalPeriodo(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Lista data/hora e total de cada venda a partir de uma data.
     *
     * @param inicio data/hora inicial do período.
     * @return linhas com data/hora e total da venda.
     */
    @Query("select v.dataHora, v.total from Venda v where v.dataHora >= :inicio order by v.dataHora")
    List<Object[]> listarDataTotalDesde(LocalDateTime inicio);

    /**
     * Lista os clientes que mais compraram a partir de uma data, ordenados pelo valor total.
     *
     * @param inicio data/hora inicial do período.
     * @param pageable limitador de resultados.
     * @return linhas com nome do cliente, quantidade de vendas e soma do total.
     */
    @Query("""
        select c.nome, count(v), sum(v.total)
        from Venda v
        join v.cliente c
        where v.dataHora >= :inicio
        group by c.nome
        order by sum(v.total) desc
        """)
    List<Object[]> topClientes(LocalDateTime inicio, Pageable pageable);

}
