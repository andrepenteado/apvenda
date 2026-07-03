/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.dto.DashboardResponse;
import com.github.andrepenteado.venda.domain.dto.VendaConsolidada;
import com.github.andrepenteado.venda.domain.dto.VendaPesquisaResponse;
import com.github.andrepenteado.venda.domain.dto.VendaRequest;
import com.github.andrepenteado.venda.domain.dto.VendaResponse;
import com.github.andrepenteado.venda.domain.filter.VendaFilter;
import com.github.andrepenteado.venda.services.VendaService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Resource REST de Venda (PDV).
 */
@Observed
@RestController
@RequestMapping("/vendas")
public class VendaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendaResource.class);

    private final VendaService service;

    /**
     * Cria o resource de Venda.
     *
     * @param service serviço de Venda.
     */
    public VendaResource(VendaService service) {
        this.service = service;
    }

    /**
     * Monta o resumo financeiro consolidado para o dashboard.
     *
     * @return resumo financeiro de vendas.
     */
    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        LOGGER.info("GET /vendas/dashboard - Dashboard financeiro de Vendas");
        return service.dashboard();
    }

    /**
     * Etapa 1 da finalização: calcula os totais e valida os itens, sem gravar.
     *
     * @param request itens do carrinho.
     * @return venda consolidada.
     */
    @PostMapping("/preparar")
    public VendaConsolidada preparar(@RequestBody VendaRequest request) {
        LOGGER.info("POST /vendas/preparar - Preparar venda");
        return service.preparar(request);
    }

    /**
     * Etapa 2 da finalização: grava a venda, os itens e o recebimento único (já
     * pago), e baixa o estoque.
     *
     * @param request itens e dados de pagamento.
     * @return venda gravada.
     */
    @PostMapping
    public VendaResponse finalizar(@RequestBody VendaRequest request) {
        LOGGER.info("POST /vendas - Finalizar venda");
        return service.finalizar(request);
    }

    /**
     * Lista todas as vendas.
     *
     * @return lista de vendas.
     */
    @GetMapping
    public List<VendaPesquisaResponse> listar() {
        LOGGER.info("GET /vendas - Listar Vendas");
        return service.listar();
    }

    /**
     * Pesquisa vendas pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return vendas encontradas.
     */
    @GetMapping("/pesquisar")
    public List<VendaPesquisaResponse> pesquisar(VendaFilter filtro) {
        LOGGER.info("GET /vendas/pesquisar - Pesquisar Vendas: idVenda={}, dataInicio={}, dataFinal={}, cpfCliente={}, consumidor={}",
            filtro.getIdVenda(), filtro.getDataInicio(), filtro.getDataFinal(), filtro.getCpfCliente(), filtro.getConsumidor());
        return service.pesquisar(filtro);
    }

    /**
     * Estorna uma venda: exclui a venda, os itens e o financeiro e devolve o
     * estoque.
     *
     * @param id identificador da venda.
     */
    @DeleteMapping("/{id}")
    public void estornar(@PathVariable Long id) {
        LOGGER.info("DELETE /vendas/{} - Estornar venda", id);
        service.estornar(id);
    }

}
