/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.resources;

import com.github.andrepenteado.venda.domain.dto.ReceberBaixaRequest;
import com.github.andrepenteado.venda.domain.dto.ReceberPesquisaResponse;
import com.github.andrepenteado.venda.domain.filter.ReceberFilter;
import com.github.andrepenteado.venda.services.ReceberService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Resource REST de Receber (contas a receber das vendas).
 */
@Observed
@RestController
@RequestMapping("/receber")
public class ReceberResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceberResource.class);

    private final ReceberService service;

    /**
     * Cria o resource de Receber.
     *
     * @param service serviço de Receber.
     */
    public ReceberResource(ReceberService service) {
        this.service = service;
    }

    /**
     * Lista todas as parcelas a receber.
     *
     * @return lista de parcelas a receber.
     */
    @GetMapping
    public List<ReceberPesquisaResponse> listar() {
        LOGGER.info("GET /receber - Listar Contas a Receber");
        return service.listar();
    }

    /**
     * Pesquisa parcelas a receber pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return parcelas a receber encontradas.
     */
    @GetMapping("/pesquisar")
    public List<ReceberPesquisaResponse> pesquisar(ReceberFilter filtro) {
        LOGGER.info("GET /receber/pesquisar - Pesquisar Contas a Receber: idVenda={}, nomeCliente={}, cpfCliente={}, dataInicio={}, dataFinal={}, emAberto={}",
            filtro.getIdVenda(), filtro.getNomeCliente(), filtro.getCpfCliente(), filtro.getDataInicio(), filtro.getDataFinal(), filtro.getEmAberto());
        return service.pesquisar(filtro);
    }

    /**
     * Dá baixa em lote nas parcelas em aberto de uma mesma venda.
     *
     * @param request parcelas selecionadas e dados do pagamento.
     * @return recebimento criado na baixa.
     */
    @PostMapping("/baixa")
    public ReceberPesquisaResponse baixar(@RequestBody ReceberBaixaRequest request) {
        LOGGER.info("POST /receber/baixa - Dar baixa em Contas a Receber: idsParcelas={}", request.idsParcelas());
        return service.baixar(request);
    }

}
