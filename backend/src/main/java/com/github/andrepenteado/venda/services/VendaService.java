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
import com.github.andrepenteado.venda.domain.dto.VendaConsolidada;
import com.github.andrepenteado.venda.domain.dto.VendaPesquisaResponse;
import com.github.andrepenteado.venda.domain.dto.VendaRequest;
import com.github.andrepenteado.venda.domain.dto.VendaResponse;
import com.github.andrepenteado.venda.domain.dto.datatables.DatatablesRequest;
import com.github.andrepenteado.venda.domain.dto.datatables.VendaDatatablesResponse;
import com.github.andrepenteado.venda.domain.entities.Cliente;
import com.github.andrepenteado.venda.domain.entities.ItemVenda;
import com.github.andrepenteado.venda.domain.entities.Produto;
import com.github.andrepenteado.venda.domain.entities.QReceber;
import com.github.andrepenteado.venda.domain.entities.QVenda;
import com.github.andrepenteado.venda.domain.entities.Receber;
import com.github.andrepenteado.venda.domain.entities.Venda;
import com.github.andrepenteado.venda.domain.enums.FormaPagamento;
import com.github.andrepenteado.venda.domain.filter.VendaFilter;
import com.github.andrepenteado.venda.domain.repositories.ClienteRepository;
import com.github.andrepenteado.venda.domain.repositories.ItemVendaRepository;
import com.github.andrepenteado.venda.domain.repositories.ProdutoRepository;
import com.github.andrepenteado.venda.domain.repositories.ReceberRepository;
import com.github.andrepenteado.venda.domain.repositories.VendaRepository;
import com.github.andrepenteado.venda.services.datatables.DatatablesSupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Serviço de regras de negócio de Venda (PDV).
 */
@Service
public class VendaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendaService.class);

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    /**
     * Whitelist de ordenação do grid: coluna do DataTables → propriedade da entidade.
     */
    private static final Map<String, String> COLUNAS_ORDENAVEIS = Map.of(
        "id", "id",
        "dataHora", "dataHora",
        "nomeCliente", "cliente.nome",
        "cpfCliente", "cliente.cpfCnpj",
        "total", "total"
    );

    private final VendaRepository repository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final ReceberRepository receberRepository;
    private final SecurityService securityService;
    private final EntityManager entityManager;

    /**
     * Cria o serviço de Venda.
     *
     * @param repository repositório de Venda.
     * @param produtoRepository repositório de Produto.
     * @param clienteRepository repositório de Cliente.
     * @param itemVendaRepository repositório de ItemVenda.
     * @param receberRepository repositório de Receber.
     * @param securityService serviço de segurança.
     * @param entityManager entity manager para consultas de agregação QueryDSL.
     */
    public VendaService(VendaRepository repository, ProdutoRepository produtoRepository,
                        ClienteRepository clienteRepository, ItemVendaRepository itemVendaRepository,
                        ReceberRepository receberRepository, SecurityService securityService,
                        EntityManager entityManager) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.receberRepository = receberRepository;
        this.securityService = securityService;
        this.entityManager = entityManager;
    }

    /**
     * Finaliza a venda: consolida e valida os itens, grava a Venda, os ItemVenda
     * e o registro único de Receber (sempre pago na data da venda) e baixa o
     * estoque, tudo na mesma transação.
     *
     * @param request itens e dados de pagamento.
     * @return venda gravada.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public VendaResponse finalizar(VendaRequest request) {
        LOGGER.info("Finalizando venda no PDV");

        VendaConsolidada consolidada = consolidar(request.itens());

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

        gerarReceber(venda, consolidada.total(), request);

        Venda salva = repository.save(venda);
        LOGGER.info("Venda #{} gravada com total {}", salva.getId(), salva.getTotal());
        return montarResposta(salva, consolidada.itens());
    }

    /**
     * Consulta paginada do grid de Vendas (server-side processing do
     * DataTables): aplica o filtro da tela, a busca global do grid e a
     * ordenação, devolvendo a página solicitada e os agregados (valor total e
     * valor pago) do resultado filtrado inteiro para os cards de resumo.
     *
     * @param request request do protocolo DataTables.
     * @param filtro filtro da tela de pesquisa.
     * @return página de vendas, contadores e agregados.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public VendaDatatablesResponse datatables(DatatablesRequest request, VendaFilter filtro) {
        LOGGER.info("Consulta datatables de Vendas: start={}, length={}", request.start(), request.length());

        QVenda venda = QVenda.venda;
        BooleanBuilder predicate = new BooleanBuilder(filtro != null ? filtro.toPredicate() : null);

        String termo = DatatablesSupport.termoBusca(request);
        if (termo != null) {
            BooleanBuilder busca = new BooleanBuilder().or(venda.cliente.nome.containsIgnoreCase(termo));
            if (termo.matches("\\d{1,18}")) {
                busca.or(venda.id.eq(Long.valueOf(termo)));
            }
            predicate.and(busca);
        }

        // Padrão: vendas mais recentes primeiro.
        Pageable pageable = DatatablesSupport.toPageable(request, COLUNAS_ORDENAVEIS, Sort.by(Sort.Direction.DESC, "id"));
        Page<Venda> pagina = repository.findAll(predicate, pageable);
        List<VendaPesquisaResponse> vendas = pagina.getContent().stream().map(this::toResponse).toList();

        // Agregados do resultado filtrado inteiro (não apenas da página), no banco.
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QReceber receber = QReceber.receber;
        BigDecimal valorTotalGeral = query.select(venda.total.sum()).from(venda).where(predicate).fetchOne();
        BigDecimal valorPagoGeral = query.select(receber.valorPago.sum())
            .from(venda)
            .join(venda.recebimentos, receber)
            .where(predicate)
            .fetchOne();

        return new VendaDatatablesResponse(
            request.draw(),
            repository.count(),
            pagina.getTotalElements(),
            vendas,
            valorTotalGeral != null ? valorTotalGeral : BigDecimal.ZERO,
            valorPagoGeral != null ? valorPagoGeral : BigDecimal.ZERO
        );
    }

    /**
     * Busca uma venda pelo identificador, com os itens vendidos, para consulta
     * somente leitura (ex.: tela de consulta e impressão do relatório de vendas).
     *
     * @param id identificador da venda.
     * @return venda encontrada, com os itens.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public VendaResponse buscar(Long id) {
        LOGGER.info("Buscando venda #{}", id);
        Venda venda = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venda não encontrada"));

        List<ItemConsolidado> itens = venda.getItens().stream()
            .map(item -> new ItemConsolidado(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getProduto().getUnidade(),
                item.getQuantidade(),
                item.getValorUnitario(),
                item.getValorTotal()
            ))
            .toList();

        return montarResposta(venda, itens);
    }

    /**
     * Estorna uma venda: exclui a venda, os itens e o financeiro (Receber) e
     * devolve as quantidades vendidas ao estoque, na mesma transação.
     *
     * @param id identificador da venda.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public void estornar(Long id) {
        LOGGER.info("Estornando venda #{}", id);
        Venda venda = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venda não encontrada"));

        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            BigDecimal atual = produto.getEstoqueAtual() == null ? BigDecimal.ZERO : produto.getEstoqueAtual();
            produto.setEstoqueAtual(atual.add(item.getQuantidade()));
        }

        // O cascade das coleções exclui itens e recebimentos junto com a venda.
        repository.delete(venda);
        LOGGER.info("Venda #{} estornada", id);
    }

    /**
     * Monta o resumo financeiro do dashboard: totais do dia e do mês, série
     * diária dos últimos 30 dias, produtos e clientes que mais venderam e
     * distribuição por forma de pagamento.
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
            itens.add(new ItemConsolidado(produto.getId(), produto.getNome(), produto.getUnidade(), req.quantidade(), valorUnitario, valorTotal));
            total = total.add(valorTotal);
        }

        return new VendaConsolidada(itens, total.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Gera o registro único de Receber da venda, sempre pago na data da venda.
     * O valor é o líquido: total + juros% - desconto%.
     */
    private void gerarReceber(Venda venda, BigDecimal total, VendaRequest request) {
        if (request.formaPagamento() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Informe a forma de pagamento");
        }

        int juros = request.juros() == null ? 0 : request.juros();
        int desconto = request.desconto() == null ? 0 : request.desconto();
        BigDecimal fator = BigDecimal.valueOf(100L + juros - desconto);
        BigDecimal liquido = total.multiply(fator).divide(CEM, 2, RoundingMode.HALF_UP);

        LocalDate hoje = LocalDate.now();
        Receber receber = new Receber();
        // parcela 0 = à vista: a venda gera um único recebimento, já quitado.
        receber.setParcela(0);
        receber.setFormaPagamento(request.formaPagamento());
        receber.setDataVencimento(hoje);
        receber.setDataPagamento(hoje);
        receber.setValorAReceber(liquido);
        receber.setValorPago(liquido);
        venda.addReceber(receber);
    }

    private VendaResponse montarResposta(Venda venda, List<ItemConsolidado> itens) {
        Receber receber = venda.getRecebimentos().getFirst();
        Cliente cliente = venda.getCliente();
        return new VendaResponse(
            venda.getId(),
            venda.getDataHora(),
            venda.getTotal(),
            cliente != null ? cliente.getNome() : null,
            cliente != null ? cliente.getTelefone() : null,
            itens,
            receber.getFormaPagamento(),
            receber.getValorPago()
        );
    }

    private VendaPesquisaResponse toResponse(Venda venda) {
        Receber receber = venda.getRecebimentos().isEmpty() ? null : venda.getRecebimentos().getFirst();
        Cliente cliente = venda.getCliente();
        return new VendaPesquisaResponse(
            venda.getId(),
            venda.getDataHora(),
            cliente != null ? cliente.getNome() : null,
            cliente != null ? cliente.getCpfCnpj() : null,
            receber != null ? receber.getFormaPagamento() : null,
            venda.getTotal(),
            receber != null ? receber.getValorPago() : null
        );
    }

}
