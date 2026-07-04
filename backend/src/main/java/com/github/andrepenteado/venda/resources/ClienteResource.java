/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.entities.Cliente;
import com.github.andrepenteado.venda.domain.filter.ClienteFilter;
import com.github.andrepenteado.venda.services.ClienteService;
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
 * Resource REST de Cliente.
 */
@Observed
@RestController
@RequestMapping("/clientes")
public class ClienteResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteResource.class);

    private final ClienteService service;

    /**
     * Cria o resource de Cliente.
     *
     * @param service serviço de Cliente.
     */
    public ClienteResource(ClienteService service) {
        this.service = service;
    }

    /**
     * Lista todos os Clientes.
     *
     * @return lista de Clientes.
     */
    @GetMapping
    public List<Cliente> listar() {
        LOGGER.info("GET /clientes - Listar Clientes");
        return service.listar();
    }

    /**
     * Pesquisa Clientes pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return Clientes encontrados.
     */
    @GetMapping("/pesquisar")
    public Iterable<Cliente> pesquisar(ClienteFilter filtro) {
        LOGGER.info("GET /clientes/pesquisar - Pesquisar Clientes: nome={}, cpfCnpj={}", filtro.getNome(), filtro.getCpfCnpj());
        return service.pesquisar(filtro);
    }

    /**
     * Busca um Cliente pelo identificador.
     *
     * @param id identificador do Cliente.
     * @return Cliente encontrado.
     */
    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Long id) {
        LOGGER.info("GET /clientes/{} - Buscar Cliente", id);
        return service.buscar(id);
    }

    /**
     * Inclui um novo Cliente.
     *
     * @param cliente Cliente a ser incluído.
     * @return Cliente incluído.
     */
    @PostMapping
    public Cliente incluir(@Valid @RequestBody Cliente cliente) {
        LOGGER.info("POST /clientes - Incluir Cliente");
        return service.incluir(cliente);
    }

    /**
     * Altera um Cliente existente.
     *
     * @param id identificador do Cliente.
     * @param cliente Cliente a ser alterado.
     * @return Cliente alterado.
     */
    @PutMapping("/{id}")
    public Cliente alterar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        LOGGER.info("PUT /clientes/{} - Alterar Cliente", id);
        return service.alterar(cliente, id);
    }

    /**
     * Exclui um Cliente pelo identificador.
     *
     * @param id identificador do Cliente.
     */
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        LOGGER.info("DELETE /clientes/{} - Excluir Cliente", id);
        service.excluir(id);
    }

}
