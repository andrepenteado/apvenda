/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.dto.datatables.DatatablesResponse;
import com.github.andrepenteado.venda.domain.dto.datatables.ProdutoDatatablesRequest;
import com.github.andrepenteado.venda.domain.entities.Produto;
import com.github.andrepenteado.venda.domain.filter.ProdutoFilter;
import com.github.andrepenteado.venda.services.ProdutoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Resource REST de Produto.
 */
@Observed
@RestController
@RequestMapping("/produtos")
public class ProdutoResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProdutoResource.class);

    private final ProdutoService service;

    /**
     * Cria o resource de Produto.
     *
     * @param service serviço de Produto.
     */
    public ProdutoResource(ProdutoService service) {
        this.service = service;
    }

    /**
     * Consulta paginada do grid de Produtos (server-side processing do
     * DataTables), combinando o filtro da tela com a busca global do grid.
     *
     * @param request request do DataTables e filtro da tela.
     * @return página de Produtos e contadores.
     */
    @PostMapping("/datatables")
    public DatatablesResponse<Produto> datatables(@RequestBody ProdutoDatatablesRequest request) {
        LOGGER.info("POST /produtos/datatables - Consulta paginada de Produtos");
        return service.datatables(request.datatables(), request.filtro() != null ? request.filtro() : new ProdutoFilter());
    }

    /**
     * Busca um Produto pelo identificador.
     *
     * @param id identificador do Produto.
     * @return Produto encontrado.
     */
    @GetMapping("/{id}")
    public Produto buscar(@PathVariable Long id) {
        LOGGER.info("GET /produtos/{} - Buscar Produto", id);
        return service.buscar(id);
    }

    /**
     * Busca produtos ativos para o PDV por código de barras ou parte do nome.
     *
     * @param termo texto pesquisado.
     * @return produtos que casam com o termo.
     */
    @GetMapping("/pdv")
    public List<Produto> buscarPdv(@RequestParam String termo) {
        LOGGER.info("GET /produtos/pdv - Buscar produtos no PDV: termo={}", termo);
        return service.buscarPdv(termo);
    }

    /**
     * Inclui um novo Produto.
     *
     * @param produto Produto a ser incluído.
     * @return Produto incluído.
     */
    @PostMapping
    public Produto incluir(@Valid @RequestBody Produto produto) {
        LOGGER.info("POST /produtos - Incluir Produto");
        return service.incluir(produto);
    }

    /**
     * Altera um Produto existente.
     *
     * @param id identificador do Produto.
     * @param produto Produto a ser alterado.
     * @return Produto alterado.
     */
    @PutMapping("/{id}")
    public Produto alterar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        LOGGER.info("PUT /produtos/{} - Alterar Produto", id);
        return service.alterar(produto, id);
    }

    /**
     * Exclui um Produto pelo identificador.
     *
     * @param id identificador do Produto.
     */
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        LOGGER.info("DELETE /produtos/{} - Excluir Produto", id);
        service.excluir(id);
    }

}
