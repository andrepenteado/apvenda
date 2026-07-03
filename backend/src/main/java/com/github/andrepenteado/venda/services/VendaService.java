/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.services;

import br.unesp.fc.andrepenteado.core.web.services.SecurityService;
import com.github.andrepenteado.venda.VendaApplication;
import com.github.andrepenteado.venda.domain.dto.DashboardResponse;
import com.github.andrepenteado.venda.domain.dto.ItemConsolidado;
import com.github.andrepenteado.venda.domain.dto.ItemVendaRequest;
import com.github.andrepenteado.venda.domain.dto.ParcelaRequest;
import com.github.andrepenteado.venda.domain.dto.ReceberResponse;
import com.github.andrepenteado.venda.domain.dto.VendaConsolidada;
import com.github.andrepenteado.venda.domain.dto.VendaRequest;
import com.github.andrepenteado.venda.domain.dto.VendaResponse;
import com.github.andrepenteado.venda.domain.entities.Cliente;
import com.github.andrepenteado.venda.domain.entities.ItemVenda;
import com.github.andrepenteado.venda.domain.entities.Produto;
import com.github.andrepenteado.venda.domain.entities.Receber;
import com.github.andrepenteado.venda.domain.entities.Venda;
import com.github.andrepenteado.venda.domain.enums.FormaPagamento;
import com.github.andrepenteado.venda.domain.repositories.ClienteRepository;
import com.github.andrepenteado.venda.domain.repositories.ItemVendaRepository;
import com.github.andrepenteado.venda.domain.repositories.ProdutoRepository;
import com.github.andrepenteado.venda.domain.repositories.ReceberRepository;
import com.github.andrepenteado.venda.domain.repositories.VendaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Serviço de regras de negócio de Venda (PDV).
 */
@Service
public class VendaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendaService.class);

    private static final int MAX_PARCELAS = 12;
    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final VendaRepository repository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final ReceberRepository receberRepository;
    private final SecurityService securityService;

    /**
     * Cria o serviço de Venda.
     *
     * @param repository repositório de Venda.
     * @param produtoRepository repositório de Produto.
     * @param clienteRepository repositório de Cliente.
     * @param itemVendaRepository repositório de ItemVenda.
     * @param receberRepository repositório de Receber.
     * @param securityService serviço de segurança.
     */
    public VendaService(VendaRepository repository, ProdutoRepository produtoRepository,
                        ClienteRepository clienteRepository, ItemVendaRepository itemVendaRepository,
                        ReceberRepository receberRepository, SecurityService securityService) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.receberRepository = receberRepository;
        this.securityService = securityService;
    }

    /**
     * Etapa 1: calcula os totais e valida os itens, sem gravar.
     *
     * @param request itens do carrinho.
     * @return venda consolidada (itens precificados e total).
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public VendaConsolidada preparar(VendaRequest request) {
        LOGGER.info("Preparando venda no PDV com {} item(ns)", request.itens() == null ? 0 : request.itens().size());
        return consolidar(request.itens());
    }

    /**
     * Etapa 2: grava a Venda, os ItemVenda e os registros de Receber (um por
     * parcela) e baixa o estoque, tudo na mesma transação.
     *
     * @param request itens e dados de pagamento.
     * @return venda gravada.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public VendaResponse finalizar(VendaRequest request) {
        LOGGER.info("Finalizando venda no PDV");

        VendaConsolidada consolidada = consolidar(request.itens());
        List<ParcelaRequest> parcelas = request.parcelas();
        if (parcelas == null || parcelas.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Informe os dados de pagamento");
        }
        if (parcelas.size() > MAX_PARCELAS) {
            throw new ResponseStatusException(BAD_REQUEST, "Número de parcelas deve ser de 1 a " + MAX_PARCELAS);
        }

        LocalDateTime agora = LocalDateTime.now();
        Venda venda = new Venda();
        venda.setDataHora(agora);
        venda.setTotal(consolidada.total());
        venda.setCriadoPor(securityService.getUserLogin().getLogin());
        venda.setCriadoEm(agora);

        // Cliente é opcional: sem vínculo, a venda fica como consumidor (fk_cliente nulo).
        if (request.cliente() != null) {
            Cliente cliente = clienteRepository.findById(request.cliente())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Cliente informado não existe"));
            venda.setCliente(cliente);
        }

        for (ItemConsolidado ic : consolidada.itens()) {
            Produto produto = produtoRepository.findById(ic.produto())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Produto informado não existe"));

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(ic.quantidade());
            item.setValorUnitario(ic.valorUnitario());
            item.setValorTotal(ic.valorTotal());
            venda.addItem(item);

            // Baixa de estoque (sem validar negativo/zero nesta fase).
            BigDecimal atual = produto.getEstoqueAtual() == null ? BigDecimal.ZERO : produto.getEstoqueAtual();
            produto.setEstoqueAtual(atual.subtract(ic.quantidade()));
        }

        gerarParcelas(venda, consolidada.total(), request);

        Venda salva = repository.save(venda);
        LOGGER.info("Venda #{} gravada com total {}", salva.getId(), salva.getTotal());
        return montarResposta(salva, consolidada.itens());
    }

    /**
     * Monta o resumo financeiro do dashboard: totais do dia e do mês, contas a
     * receber, série diária dos últimos 30 dias, produtos e clientes que mais
     * venderam e distribuição por forma de pagamento.
     *
     * @return resumo financeiro consolidado.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public DashboardResponse dashboard() {
        LOGGER.info("Montando dashboard financeiro de vendas");

        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime fimHoje = hoje.plusDays(1).atStartOfDay();
        LocalDate primeiroDiaMes = hoje.withDayOfMonth(1);
        LocalDateTime inicioMes = primeiroDiaMes.atStartOfDay();
        LocalDateTime inicio30Dias = hoje.minusDays(29).atStartOfDay();

        BigDecimal totalHoje = repository.somarTotalPeriodo(inicioHoje, fimHoje);
        long quantidadeHoje = repository.countByDataHoraGreaterThanEqualAndDataHoraLessThan(inicioHoje, fimHoje);
        BigDecimal totalMes = repository.somarTotalPeriodo(inicioMes, fimHoje);
        long quantidadeMes = repository.countByDataHoraGreaterThanEqualAndDataHoraLessThan(inicioMes, fimHoje);
        BigDecimal ticketMedioMes = quantidadeMes == 0
            ? BigDecimal.ZERO
            : totalMes.divide(BigDecimal.valueOf(quantidadeMes), 2, RoundingMode.HALF_UP);

        // Série diária contínua dos últimos 30 dias, preenchendo dias sem venda com zero.
        Map<LocalDate, DashboardResponse.VendaDia> porDia = new LinkedHashMap<>();
        for (int i = 0; i < 30; i++) {
            LocalDate dia = hoje.minusDays(29L - i);
            porDia.put(dia, new DashboardResponse.VendaDia(dia, BigDecimal.ZERO, 0));
        }
        for (Object[] linha : repository.listarDataTotalDesde(inicio30Dias)) {
            LocalDate dia = ((LocalDateTime) linha[0]).toLocalDate();
            BigDecimal total = (BigDecimal) linha[1];
            DashboardResponse.VendaDia atual = porDia.get(dia);
            if (atual != null) {
                porDia.put(dia, new DashboardResponse.VendaDia(dia, atual.total().add(total), atual.quantidade() + 1));
            }
        }

        List<DashboardResponse.TopProduto> topProdutos = itemVendaRepository.topProdutos(inicioMes, PageRequest.of(0, 5)).stream()
            .map(linha -> new DashboardResponse.TopProduto((String) linha[0], (BigDecimal) linha[1], (BigDecimal) linha[2]))
            .toList();

        List<DashboardResponse.FormaPagamentoTotal> formasPagamento = receberRepository.totaisPorFormaPagamento(inicioMes).stream()
            .map(linha -> new DashboardResponse.FormaPagamentoTotal((FormaPagamento) linha[0], (BigDecimal) linha[1]))
            .toList();

        List<DashboardResponse.TopCliente> topClientes = repository.topClientes(inicioMes, PageRequest.of(0, 5)).stream()
            .map(linha -> new DashboardResponse.TopCliente((String) linha[0], (Long) linha[1], (BigDecimal) linha[2]))
            .toList();

        return new DashboardResponse(
            totalHoje,
            quantidadeHoje,
            totalMes,
            quantidadeMes,
            ticketMedioMes,
            receberRepository.somarAberto(),
            receberRepository.somarVencido(hoje),
            receberRepository.somarRecebidoDesde(primeiroDiaMes),
            List.copyOf(porDia.values()),
            topProdutos,
            formasPagamento,
            topClientes
        );
    }

    /**
     * Precifica os itens e calcula o total, validando produto, quantidade e preço.
     */
    private VendaConsolidada consolidar(List<ItemVendaRequest> itensRequest) {
        if (itensRequest == null || itensRequest.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Informe ao menos um item na venda");
        }

        List<ItemConsolidado> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemVendaRequest req : itensRequest) {
            if (req.produto() == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Item sem produto informado");
            }
            Produto produto = produtoRepository.findById(req.produto())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Produto informado não existe"));

            if (req.quantidade() == null || req.quantidade().signum() <= 0) {
                throw new ResponseStatusException(BAD_REQUEST, "Quantidade inválida para o produto " + produto.getNome());
            }
            if (produto.getPrecoVenda() == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Produto sem preço de venda: " + produto.getNome());
            }

            BigDecimal valorUnitario = produto.getPrecoVenda();
            BigDecimal valorTotal = valorUnitario.multiply(req.quantidade()).setScale(2, RoundingMode.HALF_UP);
            itens.add(new ItemConsolidado(produto.getId(), produto.getNome(), req.quantidade(), valorUnitario, valorTotal));
            total = total.add(valorTotal);
        }

        return new VendaConsolidada(itens, total.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Gera os registros de Receber a partir das parcelas informadas. O valor a
     * receber é recalculado dividindo o valor líquido (total + juros% - desconto%)
     * pelo número de parcelas, com o arredondamento na primeira parcela.
     */
    private void gerarParcelas(Venda venda, BigDecimal total, VendaRequest request) {
        int juros = request.juros() == null ? 0 : request.juros();
        int desconto = request.desconto() == null ? 0 : request.desconto();
        List<ParcelaRequest> parcelas = request.parcelas();
        int n = parcelas.size();

        BigDecimal fator = BigDecimal.valueOf(100L + juros - desconto);
        BigDecimal liquido = total.multiply(fator).divide(CEM, 2, RoundingMode.HALF_UP);
        BigDecimal base = liquido.divide(BigDecimal.valueOf(n), 2, RoundingMode.DOWN);
        BigDecimal resto = liquido.subtract(base.multiply(BigDecimal.valueOf(n)));

        for (int i = 0; i < n; i++) {
            ParcelaRequest pr = parcelas.get(i);
            if (pr.formaPagamento() == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Informe a forma de pagamento da parcela " + (i + 1));
            }
            if (pr.dataVencimento() == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Informe a data de vencimento da parcela " + (i + 1));
            }

            // A diferença de arredondamento vai na primeira parcela.
            BigDecimal valorAReceber = i == 0 ? base.add(resto) : base;

            Receber receber = new Receber();
            // parcela 0 = à vista (1 registro); 1..N = parcelado.
            receber.setParcela(n == 1 ? 0 : i + 1);
            receber.setFormaPagamento(pr.formaPagamento());
            receber.setDataVencimento(pr.dataVencimento());
            receber.setValorAReceber(valorAReceber);
            receber.setValorPago(pr.valorPago());
            // data_pagamento derivada: preenchida (= vencimento) somente se houver valor pago.
            receber.setDataPagamento(pr.valorPago() != null ? pr.dataVencimento() : null);
            venda.addReceber(receber);
        }
    }

    private VendaResponse montarResposta(Venda venda, List<ItemConsolidado> itens) {
        List<ReceberResponse> recebimentos = venda.getRecebimentos().stream()
            .map(r -> new ReceberResponse(
                r.getParcela(),
                r.getDataVencimento(),
                r.getDataPagamento(),
                r.getFormaPagamento(),
                r.getValorAReceber(),
                r.getValorPago()
            ))
            .toList();
        String cliente = venda.getCliente() != null ? venda.getCliente().getNome() : null;
        return new VendaResponse(venda.getId(), venda.getDataHora(), venda.getTotal(), cliente, itens, recebimentos);
    }

}
