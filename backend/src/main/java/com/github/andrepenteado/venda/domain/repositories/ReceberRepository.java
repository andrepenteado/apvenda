/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 23:20:00 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.repositories;

import com.github.andrepenteado.venda.domain.entities.Receber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório de Receber.
 */
public interface ReceberRepository extends JpaRepository<Receber, Long> {

    /**
     * Totaliza o faturamento por forma de pagamento a partir de uma data.
     *
     * @param inicio data/hora inicial do período.
     * @return linhas com forma de pagamento e soma do valor a receber.
     */
    @Query("""
        select r.formaPagamento, sum(r.valorAReceber)
        from Receber r
        where r.venda.dataHora >= :inicio
        group by r.formaPagamento
        order by sum(r.valorAReceber) desc
        """)
    List<Object[]> totaisPorFormaPagamento(LocalDateTime inicio);

}
