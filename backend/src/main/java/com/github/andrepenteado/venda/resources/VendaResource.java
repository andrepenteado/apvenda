/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.dto.VendaConsolidada;
import com.github.andrepenteado.venda.domain.dto.VendaRequest;
import com.github.andrepenteado.venda.domain.dto.VendaResponse;
import com.github.andrepenteado.venda.services.VendaService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * Etapa 2 da finalização: grava a venda, os itens e as parcelas a receber, e
     * baixa o estoque.
     *
     * @param request itens e dados de pagamento.
     * @return venda gravada.
     */
    @PostMapping
    public VendaResponse finalizar(@RequestBody VendaRequest request) {
        LOGGER.info("POST /vendas - Finalizar venda");
        return service.finalizar(request);
    }

}
