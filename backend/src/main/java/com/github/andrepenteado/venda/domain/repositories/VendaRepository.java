/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.repositories;

import com.github.andrepenteado.venda.domain.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório de Venda.
 */
public interface VendaRepository extends JpaRepository<Venda, Long> {
}
