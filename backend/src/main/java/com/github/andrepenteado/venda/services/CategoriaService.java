/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.services;

import br.unesp.fc.andrepenteado.core.web.services.SecurityService;
import com.github.andrepenteado.venda.VendaApplication;
import com.github.andrepenteado.venda.domain.entities.Categoria;
import com.github.andrepenteado.venda.domain.filter.CategoriaFilter;
import com.github.andrepenteado.venda.domain.repositories.CategoriaRepository;
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
 * Serviço de regras de negócio de Categoria.
 */
@Service
public class CategoriaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository repository;
    private final SecurityService securityService;

    /**
     * Cria o serviço de Categoria.
     *
     * @param repository repositório de Categoria.
     * @param securityService serviço de segurança.
     */
    public CategoriaService(CategoriaRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    /**
     * Lista todas as Categorias.
     *
     * @return lista de Categorias.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public List<Categoria> listar() {
        LOGGER.info("Listando Categorias");
        return repository.findAll();
    }

    /**
     * Pesquisa Categorias pelos filtros informados.
     *
     * @param filtro filtros de pesquisa.
     * @return lista de Categorias encontradas.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Iterable<Categoria> pesquisar(CategoriaFilter filtro) {
        LOGGER.info(
            "Pesquisando Categorias com filtro: nome={}, ativo={}, categoriaPai={}",
            filtro.getNome(),
            filtro.getAtivo(),
            filtro.getCategoriaPai()
        );
        return repository.findAll(filtro.toPredicate());
    }

    /**
     * Busca uma Categoria pelo identificador.
     *
     * @param id identificador da Categoria.
     * @return Categoria encontrada.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Categoria buscar(Long id) {
        LOGGER.info("Buscando Categoria de ID #{}", id);
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoria de ID #" + id + " não encontrada"));
    }

    /**
     * Inclui uma nova Categoria.
     *
     * @param categoria Categoria a ser incluída.
     * @return Categoria incluída.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Categoria incluir(@Valid Categoria categoria) {
        LOGGER.info("Incluindo Categoria: {}", categoria);

        if (categoria.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Nova Categoria não deve possuir ID");
        }

        validarCategoriaPai(categoria);
        categoria.setCriadoPor(securityService.getUserLogin().getLogin());
        categoria.setCriadoEm(LocalDateTime.now());
        categoria.setAlteradoPor(null);
        categoria.setAlteradoEm(null);

        return repository.save(categoria);
    }

    /**
     * Altera uma Categoria existente.
     *
     * @param categoria Categoria com novos dados.
     * @param id identificador da Categoria.
     * @return Categoria alterada.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Categoria alterar(@Valid Categoria categoria, Long id) {
        LOGGER.info("Alterando Categoria de ID #{}: {}", id, categoria);

        if (categoria.getId() == null || !categoria.getId().equals(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "ID da Categoria não confere com o ID informado na URL");
        }

        Categoria existente = buscar(id);
        validarCategoriaPai(categoria);
        categoria.setCriadoPor(existente.getCriadoPor());
        categoria.setCriadoEm(existente.getCriadoEm());
        categoria.setAlteradoPor(securityService.getUserLogin().getLogin());
        categoria.setAlteradoEm(LocalDateTime.now());

        return repository.save(categoria);
    }

    /**
     * Exclui uma Categoria pelo identificador.
     *
     * @param id identificador da Categoria.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public void excluir(Long id) {
        LOGGER.info("Excluindo Categoria de ID #{}", id);
        Categoria categoria = buscar(id);
        repository.delete(categoria);
    }

    /**
     * Valida a Categoria pai informada.
     *
     * @param categoria Categoria validada.
     */
    private void validarCategoriaPai(Categoria categoria) {
        if (categoria.getCategoriaPai() == null || categoria.getCategoriaPai().getId() == null) {
            categoria.setCategoriaPai(null);
            return;
        }

        if (categoria.getId() != null && categoria.getId().equals(categoria.getCategoriaPai().getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Categoria Pai não pode ser a própria Categoria");
        }

        Categoria categoriaPai = buscar(categoria.getCategoriaPai().getId());
        categoria.setCategoriaPai(categoriaPai);
    }

}
