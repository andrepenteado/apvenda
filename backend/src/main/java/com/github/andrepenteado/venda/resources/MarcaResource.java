/*
 * Autor: André Penteado
 * Criado em: 26/05/2026 17:21:01 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.entities.Marca;
import com.github.andrepenteado.venda.domain.filter.MarcaFilter;
import com.github.andrepenteado.venda.services.MarcaService;
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
 * Resource REST de Marca.
 */
@Observed
@RestController
@RequestMapping("/marcas")
public class MarcaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarcaResource.class);

    private final MarcaService service;

    /**
     * Cria o resource de Marca.
     *
     * @param service serviço de Marca.
     */
    public MarcaResource(MarcaService service) {
        this.service = service;
    }

    /**
     * Lista todas as Marcas.
     *
     * @return lista de Marcas.
     */
    @GetMapping
    public List<Marca> listar() {
        LOGGER.info("GET /marcas - Listar Marcas");
        return service.listar();
    }

    /**
     * Pesquisa Marcas pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return Marcas encontradas.
     */
    @GetMapping("/pesquisar")
    public Iterable<Marca> pesquisar(MarcaFilter filtro) {
        LOGGER.info("GET /marcas/pesquisar - Pesquisar Marcas: nome={}", filtro.getNome());
        return service.pesquisar(filtro);
    }

    /**
     * Busca uma Marca pelo identificador.
     *
     * @param id identificador da Marca.
     * @return Marca encontrada.
     */
    @GetMapping("/{id}")
    public Marca buscar(@PathVariable Long id) {
        LOGGER.info("GET /marcas/{} - Buscar Marca", id);
        return service.buscar(id);
    }

    /**
     * Inclui uma nova Marca.
     *
     * @param marca Marca a ser incluída.
     * @return Marca incluída.
     */
    @PostMapping
    public Marca incluir(@Valid @RequestBody Marca marca) {
        LOGGER.info("POST /marcas - Incluir Marca");
        return service.incluir(marca);
    }

    /**
     * Altera uma Marca existente.
     *
     * @param id identificador da Marca.
     * @param marca Marca a ser alterada.
     * @return Marca alterada.
     */
    @PutMapping("/{id}")
    public Marca alterar(@PathVariable Long id, @Valid @RequestBody Marca marca) {
        LOGGER.info("PUT /marcas/{} - Alterar Marca", id);
        return service.alterar(marca, id);
    }

    /**
     * Exclui uma Marca pelo identificador.
     *
     * @param id identificador da Marca.
     */
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        LOGGER.info("DELETE /marcas/{} - Excluir Marca", id);
        service.excluir(id);
    }

}
