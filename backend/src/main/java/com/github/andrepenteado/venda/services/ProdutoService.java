/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.services;

import br.unesp.fc.andrepenteado.core.upload.UploadRepository;
import br.unesp.fc.andrepenteado.core.web.services.SecurityService;
import br.unesp.fc.andrepenteado.core.web.utils.TextoUtils;
import com.github.andrepenteado.venda.VendaApplication;
import com.github.andrepenteado.venda.domain.dto.datatables.DatatablesRequest;
import com.github.andrepenteado.venda.domain.dto.datatables.DatatablesResponse;
import com.github.andrepenteado.venda.domain.entities.Categoria;
import com.github.andrepenteado.venda.domain.entities.Marca;
import com.github.andrepenteado.venda.domain.entities.Produto;
import com.github.andrepenteado.venda.domain.entities.QProduto;
import com.github.andrepenteado.venda.domain.filter.ProdutoFilter;
import com.github.andrepenteado.venda.domain.repositories.CategoriaRepository;
import com.github.andrepenteado.venda.domain.repositories.MarcaRepository;
import com.github.andrepenteado.venda.domain.repositories.ProdutoRepository;
import com.github.andrepenteado.venda.services.datatables.DatatablesSupport;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Serviço de regras de negócio de Produto.
 */
@Service
public class ProdutoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProdutoService.class);

    /**
     * Whitelist de ordenação do grid: coluna do DataTables → propriedade da entidade.
     */
    private static final Map<String, String> COLUNAS_ORDENAVEIS = Map.of(
        "id", "id",
        "nome", "nome",
        "categoria.nome", "categoria.nome",
        "marca.nome", "marca.nome",
        "precoVenda", "precoVenda",
        "estoqueAtual", "estoqueAtual",
        "ativo", "ativo"
    );

    private final ProdutoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final UploadRepository uploadRepository;
    private final SecurityService securityService;

    /**
     * Cria o serviço de Produto.
     *
     * @param repository repositório de Produto.
     * @param categoriaRepository repositório de Categoria.
     * @param marcaRepository repositório de Marca.
     * @param uploadRepository repositório de Upload.
     * @param securityService serviço de segurança.
     */
    public ProdutoService(
        ProdutoRepository repository,
        CategoriaRepository categoriaRepository,
        MarcaRepository marcaRepository,
        UploadRepository uploadRepository,
        SecurityService securityService
    ) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
        this.uploadRepository = uploadRepository;
        this.securityService = securityService;
    }

    /**
     * Consulta paginada do grid de Produtos (server-side processing do
     * DataTables): aplica o filtro da tela, a busca global do grid e a
     * ordenação, devolvendo apenas a página solicitada.
     *
     * @param request request do protocolo DataTables.
     * @param filtro filtro da tela de pesquisa.
     * @return página de Produtos e contadores.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public DatatablesResponse<Produto> datatables(DatatablesRequest request, ProdutoFilter filtro) {
        LOGGER.info("Consulta datatables de Produtos: start={}, length={}", request.start(), request.length());

        BooleanBuilder predicate = new BooleanBuilder(filtro != null ? filtro.toPredicate() : null);

        String termo = DatatablesSupport.termoBusca(request);
        if (termo != null) {
            QProduto produto = QProduto.produto;
            predicate.and(new BooleanBuilder()
                .or(produto.pesquisa.contains(TextoUtils.normalizar(termo)))
                .or(produto.categoria.nome.containsIgnoreCase(termo))
                .or(produto.marca.nome.containsIgnoreCase(termo)));
        }

        Pageable pageable = DatatablesSupport.toPageable(request, COLUNAS_ORDENAVEIS, Sort.by("nome"));
        Page<Produto> pagina = repository.findAll(predicate, pageable);

        return new DatatablesResponse<>(request.draw(), repository.count(), pagina.getTotalElements(), pagina.getContent());
    }

    /**
     * Busca produtos ativos para o PDV por parte do nome ou do código de
     * barras, sem considerar acentos nem maiúsculas/minúsculas, limitando a
     * quantidade de resultados já no banco.
     *
     * @param termo texto pesquisado (nome ou código de barras).
     * @return produtos ativos que casam com o termo (máx. 20, por nome).
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public List<Produto> buscarPdv(String termo) {
        LOGGER.info("Buscando produtos no PDV: termo={}", termo);
        if (termo == null || termo.isBlank()) {
            return List.of();
        }
        QProduto produto = QProduto.produto;
        Predicate predicate = produto.ativo.isTrue()
            .and(produto.pesquisa.contains(TextoUtils.normalizar(termo.trim())));
        return repository.findAll(predicate, PageRequest.of(0, 20, Sort.by("nome"))).getContent();
    }

    /**
     * Busca um Produto pelo identificador.
     *
     * @param id identificador do Produto.
     * @return Produto encontrado.
     */
    @Transactional(readOnly = true)
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Produto buscar(Long id) {
        LOGGER.info("Buscando Produto de ID #{}", id);
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Produto de ID #" + id + " não encontrado"));
    }

    /**
     * Inclui um novo Produto.
     *
     * @param produto Produto a ser incluído.
     * @return Produto incluído.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Produto incluir(@Valid Produto produto) {
        LOGGER.info("Incluindo Produto: {}", produto);

        if (produto.getId() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Novo Produto não deve possuir ID");
        }

        resolverRelacionamentos(produto);
        produto.setCriadoPor(securityService.getUserLogin().getLogin());
        produto.setCriadoEm(LocalDateTime.now());
        produto.setAlteradoPor(null);
        produto.setAlteradoEm(null);

        return repository.save(produto);
    }

    /**
     * Altera um Produto existente.
     *
     * @param produto Produto com novos dados.
     * @param id identificador do Produto.
     * @return Produto alterado.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public Produto alterar(@Valid Produto produto, Long id) {
        LOGGER.info("Alterando Produto de ID #{}: {}", id, produto);

        if (produto.getId() == null || !produto.getId().equals(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "ID do Produto não confere com o ID informado na URL");
        }

        Produto existente = buscar(id);
        resolverRelacionamentos(produto);
        produto.setCriadoPor(existente.getCriadoPor());
        produto.setCriadoEm(existente.getCriadoEm());
        produto.setAlteradoPor(securityService.getUserLogin().getLogin());
        produto.setAlteradoEm(LocalDateTime.now());

        return repository.save(produto);
    }

    /**
     * Exclui um Produto pelo identificador.
     *
     * @param id identificador do Produto.
     */
    @Transactional
    @Secured(VendaApplication.PERFIL_CAIXA)
    public void excluir(Long id) {
        LOGGER.info("Excluindo Produto de ID #{}", id);
        Produto produto = buscar(id);
        repository.delete(produto);
    }

    /**
     * Resolve os relacionamentos (Categoria, Marca e Foto) do Produto,
     * carregando as entidades gerenciadas a partir dos identificadores informados.
     *
     * @param produto Produto cujos relacionamentos serão resolvidos.
     */
    private void resolverRelacionamentos(Produto produto) {
        if (produto.getCategoria() == null || produto.getCategoria().getId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Categoria é um campo obrigatório");
        }
        Categoria categoria = categoriaRepository.findById(produto.getCategoria().getId())
            .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Categoria informada não existe"));
        produto.setCategoria(categoria);

        if (produto.getMarca() == null || produto.getMarca().getId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Marca é um campo obrigatório");
        }
        Marca marca = marcaRepository.findById(produto.getMarca().getId())
            .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Marca informada não existe"));
        produto.setMarca(marca);

        if (produto.getFoto() != null && !uploadRepository.existsById(produto.getFoto())) {
            throw new ResponseStatusException(BAD_REQUEST, "Foto informada não existe");
        }
    }

}
