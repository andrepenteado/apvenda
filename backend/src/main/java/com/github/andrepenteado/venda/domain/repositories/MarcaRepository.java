// André Penteado, 2026-05-06T00:00:00-03:00 - Repositório de Marca criado com ajuda da IA.
package com.github.andrepenteado.venda.domain.repositories;

import com.github.andrepenteado.venda.domain.entities.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório de persistência da Marca.
 */
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    /**
     * Pesquisa Marcas pela descrição, ignorando maiúsculas e minúsculas.
     *
     * @param descricao trecho da descrição pesquisada
     * @return Marcas encontradas
     */
    List<Marca> findByDescricaoContainingIgnoreCase(String descricao);

}
