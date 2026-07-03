/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.services;

import com.github.andrepenteado.venda.VendaApplication;
import com.github.andrepenteado.venda.domain.dto.ReceberBaixaRequest;
import com.github.andrepenteado.venda.domain.dto.ReceberPesquisaResponse;
import com.github.andrepenteado.venda.domain.entities.Cliente;
import com.github.andrepenteado.venda.domain.entities.Receber;
import com.github.andrepenteado.venda.domain.entities.Venda;
import com.github.andrepenteado.venda.domain.filter.ReceberFilter;
import com.github.andrepenteado.venda.domain.repositories.ReceberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Serviço de regras de negócio de Receber (contas a receber das vendas).
 */
@Service
public class ReceberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceberService.class);

    private final ReceberRepository repository;

    /**
     * Cria o serviço de Receber.
     *
     * @param repository repositório de Receber.
     */
    public ReceberService(ReceberRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todas as parcelas a receber.
     *
     * @return lista de parcelas a receber.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public List<ReceberPesquisaResponse> listar() {
        LOGGER.info("Listando Contas a Receber");
        return repository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * Pesquisa parcelas a receber pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return lista de parcelas a receber encontradas.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public List<ReceberPesquisaResponse> pesquisar(ReceberFilter filtro) {
        LOGGER.info("Pesquisando Contas a Receber com filtro: idVenda={}, nomeCliente={}, cpfCliente={}, dataInicio={}, dataFinal={}, emAberto={}",
            filtro.getIdVenda(), filtro.getNomeCliente(), filtro.getCpfCliente(), filtro.getDataInicio(), filtro.getDataFinal(), filtro.getEmAberto());

        List<ReceberPesquisaResponse> resultado = new ArrayList<>();
        repository.findAll(filtro.toPredicate()).forEach(receber -> resultado.add(toResponse(receber)));
        return resultado;
    }

    /**
     * Dá baixa em lote nas parcelas em aberto de uma mesma venda: as parcelas
     * informadas são excluídas e um único registro de recebimento é criado com
     * a soma dos valores a receber e os dados de pagamento informados.
     *
     * @param request parcelas selecionadas e dados do pagamento.
     * @return recebimento criado na baixa.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public ReceberPesquisaResponse baixar(ReceberBaixaRequest request) {
        LOGGER.info("Dando baixa em Contas a Receber: idsParcelas={}, dataPagamento={}, formaPagamento={}, valorPago={}",
            request.idsParcelas(), request.dataPagamento(), request.formaPagamento(), request.valorPago());

        if (request.idsParcelas() == null || request.idsParcelas().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Informe ao menos uma parcela para dar baixa");
        }

        if (request.dataPagamento() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Data de pagamento é obrigatória");
        }

        if (request.formaPagamento() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Forma de pagamento é obrigatória");
        }

        if (request.valorPago() == null || request.valorPago().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Valor pago deve ser maior que zero");
        }

        List<Long> ids = new ArrayList<>(new HashSet<>(request.idsParcelas()));
        List<Receber> parcelas = repository.findAllById(ids);

        if (parcelas.size() != ids.size()) {
            throw new ResponseStatusException(NOT_FOUND, "Uma ou mais parcelas informadas não foram encontradas");
        }

        for (Receber parcela : parcelas) {
            if (parcela.getValorPago() != null) {
                throw new ResponseStatusException(BAD_REQUEST, "Parcela de ID #" + parcela.getId() + " já está paga");
            }
        }

        long totalVendas = parcelas.stream()
            .map(parcela -> parcela.getVenda().getId())
            .distinct()
            .count();

        if (totalVendas > 1) {
            throw new ResponseStatusException(BAD_REQUEST, "Só é permitido dar baixa em lote de parcelas da mesma venda");
        }

        Venda venda = parcelas.getFirst().getVenda();

        BigDecimal valorAReceber = parcelas.stream()
            .map(Receber::getValorAReceber)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer menorParcela = parcelas.stream()
            .map(Receber::getParcela)
            .min(Comparator.naturalOrder())
            .orElseThrow();

        LocalDate menorVencimento = parcelas.stream()
            .map(Receber::getDataVencimento)
            .min(Comparator.naturalOrder())
            .orElseThrow();

        repository.deleteAll(parcelas);

        Receber recebimento = new Receber();
        recebimento.setVenda(venda);
        recebimento.setParcela(menorParcela);
        recebimento.setDataVencimento(menorVencimento);
        recebimento.setDataPagamento(request.dataPagamento());
        recebimento.setFormaPagamento(request.formaPagamento());
        recebimento.setValorAReceber(valorAReceber);
        recebimento.setValorPago(request.valorPago());

        return toResponse(repository.save(recebimento));
    }

    /**
     * Converte a entidade Receber no DTO de pesquisa, com os dados do cliente da venda.
     *
     * @param receber parcela a receber.
     * @return DTO de pesquisa da parcela.
     */
    private ReceberPesquisaResponse toResponse(Receber receber) {
        Venda venda = receber.getVenda();
        Cliente cliente = venda.getCliente();

        return new ReceberPesquisaResponse(
            receber.getId(),
            venda.getId(),
            cliente != null ? cliente.getNome() : null,
            cliente != null ? cliente.getCpf() : null,
            receber.getParcela(),
            receber.getDataVencimento(),
            receber.getDataPagamento(),
            receber.getFormaPagamento(),
            receber.getValorAReceber(),
            receber.getValorPago()
        );
    }

}
