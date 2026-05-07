// André Penteado, 2026-05-06T00:00:00-03:00 - Resource REST de Marca criado com ajuda da IA.
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.entities.Marca;
import com.github.andrepenteado.venda.services.MarcaService;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Resource REST responsável pelos endpoints de Marca.
 */
@Observed
@RestController
@RequestMapping("/marcas")
public class MarcaResource {

    private static final Logger log = LoggerFactory.getLogger(MarcaResource.class);

    private final MarcaService servico;

    /**
     * Cria o resource com o serviço da Marca.
     *
     * @param servico serviço da Marca
     */
    public MarcaResource(MarcaService servico) {
        this.servico = servico;
    }

    /**
     * Lista todas as Marcas.
     *
     * @return lista de Marcas
     */
    @GetMapping
    public List<Marca> listar() {
        log.info("GET /marcas - Listando Marca...");
        return servico.listar();
    }

    /**
     * Pesquisa Marcas pelos campos permitidos.
     *
     * @param campo campo pesquisado
     * @param valor valor pesquisado
     * @return Marcas encontradas
     */
    @GetMapping("/pesquisar")
    public List<Marca> pesquisar(@RequestParam String campo, @RequestParam String valor) {
        log.info("GET /marcas/pesquisar - Pesquisando Marca por {}={}", campo, valor);
        return servico.pesquisar(campo, valor);
    }

    /**
     * Busca uma Marca pelo identificador.
     *
     * @param id identificador da Marca
     * @return Marca encontrada
     */
    @GetMapping("/{id}")
    public Marca buscar(@PathVariable Long id) {
        log.info("GET /marcas/{} - Buscando Marca...", id);
        return servico.buscar(id);
    }

    /**
     * Inclui uma nova Marca.
     *
     * @param marca Marca a ser incluída
     * @return Marca incluída
     */
    @PostMapping
    public Marca incluir(@Valid @RequestBody Marca marca) {
        log.info("POST /marcas - Incluindo Marca...");
        return servico.incluir(marca);
    }

    /**
     * Altera uma Marca existente.
     *
     * @param id identificador da Marca
     * @param marca Marca com os novos dados
     * @return Marca alterada
     */
    @PutMapping("/{id}")
    public Marca alterar(@PathVariable Long id, @Valid @RequestBody Marca marca) {
        log.info("PUT /marcas/{} - Alterando Marca...", id);
        return servico.alterar(marca, id);
    }

    /**
     * Exclui uma Marca pelo identificador.
     *
     * @param id identificador da Marca
     */
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        log.info("DELETE /marcas/{} - Excluindo Marca...", id);
        servico.excluir(id);
    }

}
