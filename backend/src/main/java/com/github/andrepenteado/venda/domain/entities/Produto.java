/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.entities;

import com.github.andrepenteado.venda.domain.enums.Unidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um Produto.
 */
@Entity
public class Produto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é um campo obrigatório")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String nome;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @NotNull(message = "Categoria é um campo obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_categoria")
    private Categoria categoria;

    @NotNull(message = "Marca é um campo obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_marca")
    private Marca marca;

    @NotNull(message = "Unidade é um campo obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unidade unidade;

    @Column(name = "preco_venda")
    private BigDecimal precoVenda;

    @Column(name = "custo_compra")
    private BigDecimal custoCompra;

    @Column(name = "estoque_atual")
    private BigDecimal estoqueAtual;

    @NotNull(message = "Ativo? é um campo obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    // Referência (UUID) ao anexo na tabela `upload` da lib APcore. Mapeado como
    // coluna UUID (e não @ManyToOne) porque a lib não fornece o metamodelo QUpload
    // exigido pelo QueryDSL ao varrer esta entidade.
    @Column(name = "fk_foto")
    private UUID foto;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criado_por", nullable = false)
    private String criadoPor;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "alterado_por")
    private String alteradoPor;

    @Column(name = "alterado_em")
    private LocalDateTime alteradoEm;

    /**
     * Retorna o identificador do Produto.
     *
     * @return identificador do Produto.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador do Produto.
     *
     * @param id identificador do Produto.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome do Produto.
     *
     * @return nome do Produto.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do Produto.
     *
     * @param nome nome do Produto.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o código de barras do Produto.
     *
     * @return código de barras do Produto.
     */
    public String getCodigoBarras() {
        return codigoBarras;
    }

    /**
     * Define o código de barras do Produto.
     *
     * @param codigoBarras código de barras do Produto.
     */
    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    /**
     * Retorna a Categoria do Produto.
     *
     * @return Categoria do Produto.
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Define a Categoria do Produto.
     *
     * @param categoria Categoria do Produto.
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Retorna a Marca do Produto.
     *
     * @return Marca do Produto.
     */
    public Marca getMarca() {
        return marca;
    }

    /**
     * Define a Marca do Produto.
     *
     * @param marca Marca do Produto.
     */
    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    /**
     * Retorna a unidade de medida do Produto.
     *
     * @return unidade de medida do Produto.
     */
    public Unidade getUnidade() {
        return unidade;
    }

    /**
     * Define a unidade de medida do Produto.
     *
     * @param unidade unidade de medida do Produto.
     */
    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    /**
     * Retorna o preço de venda do Produto.
     *
     * @return preço de venda do Produto.
     */
    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    /**
     * Define o preço de venda do Produto.
     *
     * @param precoVenda preço de venda do Produto.
     */
    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    /**
     * Retorna o custo de compra do Produto.
     *
     * @return custo de compra do Produto.
     */
    public BigDecimal getCustoCompra() {
        return custoCompra;
    }

    /**
     * Define o custo de compra do Produto.
     *
     * @param custoCompra custo de compra do Produto.
     */
    public void setCustoCompra(BigDecimal custoCompra) {
        this.custoCompra = custoCompra;
    }

    /**
     * Retorna o estoque atual do Produto.
     *
     * @return estoque atual do Produto.
     */
    public BigDecimal getEstoqueAtual() {
        return estoqueAtual;
    }

    /**
     * Define o estoque atual do Produto.
     *
     * @param estoqueAtual estoque atual do Produto.
     */
    public void setEstoqueAtual(BigDecimal estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    /**
     * Retorna se o Produto está ativo.
     *
     * @return verdadeiro quando o Produto está ativo.
     */
    public Boolean getAtivo() {
        return ativo;
    }

    /**
     * Define se o Produto está ativo.
     *
     * @param ativo verdadeiro quando o Produto está ativo.
     */
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * Retorna o identificador (UUID) da foto do Produto.
     *
     * @return UUID da foto do Produto.
     */
    public UUID getFoto() {
        return foto;
    }

    /**
     * Define o identificador (UUID) da foto do Produto.
     *
     * @param foto UUID da foto do Produto.
     */
    public void setFoto(UUID foto) {
        this.foto = foto;
    }

    /**
     * Retorna a observação do Produto.
     *
     * @return observação do Produto.
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * Define a observação do Produto.
     *
     * @param observacao observação do Produto.
     */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    /**
     * Retorna o usuário que criou o Produto.
     *
     * @return usuário que criou o Produto.
     */
    public String getCriadoPor() {
        return criadoPor;
    }

    /**
     * Define o usuário que criou o Produto.
     *
     * @param criadoPor usuário que criou o Produto.
     */
    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    /**
     * Retorna a data de criação do Produto.
     *
     * @return data de criação do Produto.
     */
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    /**
     * Define a data de criação do Produto.
     *
     * @param criadoEm data de criação do Produto.
     */
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    /**
     * Retorna o usuário que alterou o Produto.
     *
     * @return usuário que alterou o Produto.
     */
    public String getAlteradoPor() {
        return alteradoPor;
    }

    /**
     * Define o usuário que alterou o Produto.
     *
     * @param alteradoPor usuário que alterou o Produto.
     */
    public void setAlteradoPor(String alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    /**
     * Retorna a data de alteração do Produto.
     *
     * @return data de alteração do Produto.
     */
    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    /**
     * Define a data de alteração do Produto.
     *
     * @param alteradoEm data de alteração do Produto.
     */
    public void setAlteradoEm(LocalDateTime alteradoEm) {
        this.alteradoEm = alteradoEm;
    }

    /**
     * Compara Produto por identificador.
     *
     * @param o objeto comparado.
     * @return verdadeiro quando os identificadores são iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produto produto)) {
            return false;
        }
        return Objects.equals(id, produto.id);
    }

    /**
     * Calcula o hash do Produto pelo identificador.
     *
     * @return hash do Produto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retorna uma representação textual do Produto.
     *
     * @return representação textual do Produto.
     */
    @Override
    public String toString() {
        return "Produto{" +
            "id=" + id +
            ", nome='" + nome + '\'' +
            ", unidade=" + unidade +
            ", ativo=" + ativo +
            '}';
    }

}
