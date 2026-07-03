/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 23:20:00 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.repositories;

import com.github.andrepenteado.venda.domain.entities.ItemVenda;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório de ItemVenda.
 */
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {

    /**
     * Lista os produtos mais vendidos a partir de uma data, ordenados pelo valor total.
     *
     * @param inicio data/hora inicial do período.
     * @param pageable limitador de resultados.
     * @return linhas com nome do produto, soma da quantidade e soma do valor total.
     */
    @Query("""
        select i.produto.nome, sum(i.quantidade), sum(i.valorTotal)
        from ItemVenda i
        where i.venda.dataHora >= :inicio
        group by i.produto.nome
        order by sum(i.valorTotal) desc
        """)
    List<Object[]> topProdutos(LocalDateTime inicio, Pageable pageable);

}
