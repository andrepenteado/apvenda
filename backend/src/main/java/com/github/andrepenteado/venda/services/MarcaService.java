/*
 * Autor: André Penteado
 * Criado em: 26/05/2026 17:21:01 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.services;

import br.unesp.fc.andrepenteado.core.web.services.SecurityService;
import com.github.andrepenteado.venda.VendaApplication;
import com.github.andrepenteado.venda.domain.entities.Marca;
import com.github.andrepenteado.venda.domain.filter.MarcaFilter;
import com.github.andrepenteado.venda.domain.repositories.MarcaRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Serviço de regras de negócio de Marca.
 */
@Service
public class MarcaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarcaService.class);

    private final MarcaRepository repository;
    private final SecurityService securityService;

    /**
     * Cria o serviço de Marca.
     *
     * @param repository repositório de Marca.
     * @param securityService serviço de segurança.
     */
    public MarcaService(MarcaRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    /**
     * Lista todas as Marcas.
     *
     * @return lista de Marcas.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public List<Marca> listar() {
        LOGGER.info("Listando Marcas");
        return repository.findAll();
    }

    /**
     * Pesquisa Marcas pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return lista de Marcas encontradas.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Iterable<Marca> pesquisar(MarcaFilter filtro) {
        LOGGER.info("Pesquisando Marcas com filtro: descricao={}", filtro.getDescricao());
        return repository.findAll(filtro.toPredicate());
    }

    /**
     * Busca uma Marca pelo identificador.
     *
     * @param id identificador da Marca.
     * @return Marca encontrada.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Marca buscar(Long id) {
        LOGGER.info("Buscando Marca de ID #{}", id);
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Marca de ID #" + id + " não encontrada"));
    }

    /**
     * Inclui uma nova Marca.
     *
     * @param marca Marca a ser incluída.
     * @return Marca incluída.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Marca incluir(@Valid Marca marca) {
        LOGGER.info("Incluindo Marca: {}", marca);

        if (marca.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Nova Marca não deve possuir ID");
        }

        marca.setCriadoPor(securityService.getUserLogin().getLogin());
        marca.setCriadoEm(LocalDateTime.now());
        marca.setAlteradoPor(null);
        marca.setAlteradoEm(null);

        return repository.save(marca);
    }

    /**
     * Altera uma Marca existente.
     *
     * @param marca Marca com novos dados.
     * @param id identificador da Marca.
     * @return Marca alterada.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Marca alterar(@Valid Marca marca, Long id) {
        LOGGER.info("Alterando Marca de ID #{}: {}", id, marca);

        if (marca.getId() == null || !marca.getId().equals(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "ID da Marca não confere com o ID informado na URL");
        }

        Marca existente = buscar(id);
        marca.setCriadoPor(existente.getCriadoPor());
        marca.setCriadoEm(existente.getCriadoEm());
        marca.setAlteradoPor(securityService.getUserLogin().getLogin());
        marca.setAlteradoEm(LocalDateTime.now());

        return repository.save(marca);
    }

    /**
     * Exclui uma Marca pelo identificador.
     *
     * @param id identificador da Marca.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public void excluir(Long id) {
        LOGGER.info("Excluindo Marca de ID #{}", id);
        Marca marca = buscar(id);
        repository.delete(marca);
    }

}
