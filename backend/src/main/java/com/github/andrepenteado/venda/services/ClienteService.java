/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.services;

import br.unesp.fc.andrepenteado.core.web.services.SecurityService;
import com.github.andrepenteado.venda.VendaApplication;
import com.github.andrepenteado.venda.domain.entities.Cliente;
import com.github.andrepenteado.venda.domain.filter.ClienteFilter;
import com.github.andrepenteado.venda.domain.repositories.ClienteRepository;
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
 * Serviço de regras de negócio de Cliente.
 */
@Service
public class ClienteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository repository;
    private final SecurityService securityService;

    /**
     * Cria o serviço de Cliente.
     *
     * @param repository repositório de Cliente.
     * @param securityService serviço de segurança.
     */
    public ClienteService(ClienteRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    /**
     * Lista todos os Clientes.
     *
     * @return lista de Clientes.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public List<Cliente> listar() {
        LOGGER.info("Listando Clientes");
        return repository.findAll();
    }

    /**
     * Pesquisa Clientes pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return lista de Clientes encontrados.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Iterable<Cliente> pesquisar(ClienteFilter filtro) {
        LOGGER.info("Pesquisando Clientes com filtro: nome={}, cpf={}", filtro.getNome(), filtro.getCpf());
        return repository.findAll(filtro.toPredicate());
    }

    /**
     * Busca um Cliente pelo identificador.
     *
     * @param id identificador do Cliente.
     * @return Cliente encontrado.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Cliente buscar(Long id) {
        LOGGER.info("Buscando Cliente de ID #{}", id);
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente de ID #" + id + " não encontrado"));
    }

    /**
     * Inclui um novo Cliente.
     *
     * @param cliente Cliente a ser incluído.
     * @return Cliente incluído.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Cliente incluir(@Valid Cliente cliente) {
        LOGGER.info("Incluindo Cliente: {}", cliente);

        if (cliente.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Novo Cliente não deve possuir ID");
        }

        cliente.setCriadoPor(securityService.getUserLogin().getLogin());
        cliente.setCriadoEm(LocalDateTime.now());
        cliente.setAlteradoPor(null);
        cliente.setAlteradoEm(null);

        return repository.save(cliente);
    }

    /**
     * Altera um Cliente existente.
     *
     * @param cliente Cliente com novos dados.
     * @param id identificador do Cliente.
     * @return Cliente alterado.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Cliente alterar(@Valid Cliente cliente, Long id) {
        LOGGER.info("Alterando Cliente de ID #{}: {}", id, cliente);

        if (cliente.getId() == null || !cliente.getId().equals(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "ID do Cliente não confere com o ID informado na URL");
        }

        Cliente existente = buscar(id);
        cliente.setCriadoPor(existente.getCriadoPor());
        cliente.setCriadoEm(existente.getCriadoEm());
        cliente.setAlteradoPor(securityService.getUserLogin().getLogin());
        cliente.setAlteradoEm(LocalDateTime.now());

        return repository.save(cliente);
    }

    /**
     * Exclui um Cliente pelo identificador.
     *
     * @param id identificador do Cliente.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public void excluir(Long id) {
        LOGGER.info("Excluindo Cliente de ID #{}", id);
        Cliente cliente = buscar(id);
        repository.delete(cliente);
    }

}
