/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 23:20:00 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.repositories;

import com.github.andrepenteado.venda.domain.entities.Receber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório de Receber.
 */
public interface ReceberRepository extends JpaRepository<Receber, Long>, QuerydslPredicateExecutor<Receber> {

    /**
     * Soma as parcelas ainda não pagas.
     *
     * @return total em aberto.
     */
    @Query("select coalesce(sum(r.valorAReceber), 0) from Receber r where r.valorPago is null")
    BigDecimal somarAberto();

    /**
     * Soma as parcelas não pagas já vencidas.
     *
     * @param hoje data de referência.
     * @return total vencido.
     */
    @Query("select coalesce(sum(r.valorAReceber), 0) from Receber r where r.valorPago is null and r.dataVencimento < :hoje")
    BigDecimal somarVencido(LocalDate hoje);

    /**
     * Soma os valores efetivamente pagos a partir de uma data.
     *
     * @param inicio data inicial do período.
     * @return total recebido no período.
     */
    @Query("select coalesce(sum(r.valorPago), 0) from Receber r where r.dataPagamento >= :inicio")
    BigDecimal somarRecebidoDesde(LocalDate inicio);

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
