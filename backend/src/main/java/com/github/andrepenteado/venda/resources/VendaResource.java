/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.dto.DashboardResponse;
import com.github.andrepenteado.venda.domain.dto.VendaRequest;
import com.github.andrepenteado.venda.domain.dto.VendaResponse;
import com.github.andrepenteado.venda.domain.dto.datatables.VendaDatatablesRequest;
import com.github.andrepenteado.venda.domain.dto.datatables.VendaDatatablesResponse;
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
     * Finaliza a venda: consolida e valida os itens, grava a venda, os itens e o
     * recebimento único (já pago), e baixa o estoque.
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
     * Consulta paginada do grid de Vendas (server-side processing do
     * DataTables), combinando o filtro da tela com a busca global do grid e
     * devolvendo os agregados do resultado filtrado.
     *
     * @param request request do DataTables e filtro da tela.
     * @return página de vendas, contadores e agregados.
     */
    @PostMapping("/datatables")
    public VendaDatatablesResponse datatables(@RequestBody VendaDatatablesRequest request) {
        LOGGER.info("POST /vendas/datatables - Consulta paginada de Vendas");
        return service.datatables(request.datatables(), request.filtro() != null ? request.filtro() : new VendaFilter());
    }

    /**
     * Busca uma venda pelo identificador, com os itens vendidos.
     *
     * @param id identificador da venda.
     * @return venda encontrada.
     */
    @GetMapping("/{id}")
    public VendaResponse buscar(@PathVariable Long id) {
        LOGGER.info("GET /vendas/{} - Buscar venda", id);
        return service.buscar(id);
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
