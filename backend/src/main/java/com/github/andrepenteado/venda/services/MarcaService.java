// André Penteado, 2026-05-06T00:00:00-03:00 - Serviço de Marca criado com ajuda da IA.
package com.github.andrepenteado.venda.services;

import br.unesp.fc.andrepenteado.core.web.services.SecurityService;
import com.github.andrepenteado.venda.VendaApplication;
import com.github.andrepenteado.venda.domain.entities.Marca;
import com.github.andrepenteado.venda.domain.repositories.MarcaRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio da Marca.
 */
@Service
@Validated
public class MarcaService {

    private static final Logger log = LoggerFactory.getLogger(MarcaService.class);

    private final MarcaRepository repositorio;

    private final SecurityService securityService;

    /**
     * Cria o serviço com o repositório da Marca.
     *
     * @param repositorio repositório da Marca
     */
    public MarcaService(MarcaRepository repositorio, SecurityService securityService) {
        this.repositorio = repositorio;
        this.securityService = securityService;
    }

    /**
     * Lista todas as Marcas.
     *
     * @return lista de Marcas
     */
    @Secured({VendaApplication.PERFIL_CAIXA})
    public List<Marca> listar() {
        log.info("Listando Marca...");
        return repositorio.findAll();
    }

    /**
     * Busca uma Marca pelo identificador.
     *
     * @param id identificador da Marca
     * @return Marca encontrada
     */
    @Secured({VendaApplication.PERFIL_CAIXA})
    public Marca buscar(Long id) {
        log.info("Buscando Marca por id {}...", id);
        return repositorio.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada."));
    }

    /**
     * Inclui uma nova Marca.
     *
     * @param marca Marca a ser incluída
     * @return Marca incluída
     */
    @Secured({VendaApplication.PERFIL_CAIXA})
    public Marca incluir(@Valid Marca marca) {
        log.info("Incluindo Marca...");
        if (marca.getId() != null) {
            throw new IllegalArgumentException("Marca nova não pode possuir id.");
        }
        marca.setCriadoPor(securityService.getUserLogin().getLogin());
        marca.setCriadoEm(LocalDateTime.now());
        marca.setAlteradoPor(null);
        marca.setAlteradoEm(null);
        return repositorio.save(marca);
    }

    /**
     * Altera uma Marca existente.
     *
     * @param marca Marca com os novos dados
     * @param id identificador da Marca
     * @return Marca alterada
     */
    @Secured({VendaApplication.PERFIL_CAIXA})
    public Marca alterar(@Valid Marca marca, Long id) {
        log.info("Alterando Marca id {}...", id);
        if (marca.getId() == null || !marca.getId().equals(id)) {
            throw new IllegalArgumentException("Id da Marca divergente.");
        }
        Marca existente = buscar(id);
        marca.setCriadoPor(existente.getCriadoPor());
        marca.setCriadoEm(existente.getCriadoEm());
        marca.setAlteradoPor(securityService.getUserLogin().getLogin());
        marca.setAlteradoEm(LocalDateTime.now());
        return repositorio.save(marca);
    }

    /**
     * Exclui uma Marca pelo identificador.
     *
     * @param id identificador da Marca
     */
    @Secured({VendaApplication.PERFIL_CAIXA})
    public void excluir(Long id) {
        log.info("Excluindo Marca id {}...", id);
        repositorio.deleteById(id);
    }

    /**
     * Pesquisa Marcas por campos permitidos.
     *
     * @param campo campo pesquisado
     * @param valor valor pesquisado
     * @return Marcas encontradas
     */
    @Secured({VendaApplication.PERFIL_CAIXA})
    public List<Marca> pesquisar(String campo, String valor) {
        log.info("Pesquisando Marca por campo {} e valor {}...", campo, valor);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe um valor para pesquisar Marca.");
        }
        return switch (campo) {
            case "descricao" -> repositorio.findByDescricaoContainingIgnoreCase(valor);
            default -> throw new IllegalArgumentException("Campo de pesquisa inválido para Marca.");
        };
    }

}
