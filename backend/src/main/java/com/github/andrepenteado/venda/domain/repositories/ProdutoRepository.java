/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.repositories;

import com.github.andrepenteado.venda.domain.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Repositório de Produto.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long>, QuerydslPredicateExecutor<Produto> {
}
