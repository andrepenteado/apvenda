/*
 * Autor: André Penteado
 * Criado em: 26/05/2026 17:21:01 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.repositories;

import com.github.andrepenteado.venda.domain.entities.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Repositório de Marca.
 */
public interface MarcaRepository extends JpaRepository<Marca, Long>, QuerydslPredicateExecutor<Marca> {

}
