/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.entities.Categoria;
import com.github.andrepenteado.venda.domain.filter.CategoriaFilter;
import com.github.andrepenteado.venda.services.CategoriaService;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Resource REST de Categoria.
 */
@Observed
@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriaResource.class);

    private final CategoriaService service;

    /**
     * Cria o resource de Categoria.
     *
     * @param service serviço de Categoria.
     */
    public CategoriaResource(CategoriaService service) {
        this.service = service;
    }

    /**
     * Lista todas as Categorias.
     *
     * @return lista de Categorias.
     */
    @GetMapping
    public List<Categoria> listar() {
        LOGGER.info("GET /categorias - Listar Categorias");
        return service.listar();
    }

    /**
     * Pesquisa Categorias pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return Categorias encontradas.
     */
    @GetMapping("/pesquisar")
    public Iterable<Categoria> pesquisar(CategoriaFilter filtro) {
        LOGGER.info(
            "GET /categorias/pesquisar - Pesquisar Categorias: nome={}, ativo={}, categoriaPai={}",
            filtro.getNome(),
            filtro.getAtivo(),
            filtro.getCategoriaPai()
        );
        return service.pesquisar(filtro);
    }

    /**
     * Busca uma Categoria pelo identificador.
     *
     * @param id identificador da Categoria.
     * @return Categoria encontrada.
     */
    @GetMapping("/{id}")
    public Categoria buscar(@PathVariable Long id) {
        LOGGER.info("GET /categorias/{} - Buscar Categoria", id);
        return service.buscar(id);
    }

    /**
     * Inclui uma nova Categoria.
     *
     * @param categoria Categoria a ser incluída.
     * @return Categoria incluída.
     */
    @PostMapping
    public Categoria incluir(@Valid @RequestBody Categoria categoria) {
        LOGGER.info("POST /categorias - Incluir Categoria");
        return service.incluir(categoria);
    }

    /**
     * Altera uma Categoria existente.
     *
     * @param id identificador da Categoria.
     * @param categoria Categoria a ser alterada.
     * @return Categoria alterada.
     */
    @PutMapping("/{id}")
    public Categoria alterar(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {
        LOGGER.info("PUT /categorias/{} - Alterar Categoria", id);
        return service.alterar(categoria, id);
    }

    /**
     * Exclui uma Categoria pelo identificador.
     *
     * @param id identificador da Categoria.
     */
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        LOGGER.info("DELETE /categorias/{} - Excluir Categoria", id);
        service.excluir(id);
    }

}
