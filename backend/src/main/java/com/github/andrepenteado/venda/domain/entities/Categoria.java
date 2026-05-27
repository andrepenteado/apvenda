/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa uma Categoria.
 */
@Entity
public class Categoria implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é um campo obrigatório")
    @Column(nullable = false, unique = true)
    private String nome;

    @NotNull(message = "Ativo? é um campo obrigatório")
    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_categoria_pai")
    private Categoria categoriaPai;

    @Column(name = "criado_por", nullable = false)
    private String criadoPor;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "alterado_por")
    private String alteradoPor;

    @Column(name = "alterado_em")
    private LocalDateTime alteradoEm;

    /**
     * Retorna o identificador da Categoria.
     *
     * @return identificador da Categoria.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador da Categoria.
     *
     * @param id identificador da Categoria.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome da Categoria.
     *
     * @return nome da Categoria.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da Categoria.
     *
     * @param nome nome da Categoria.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna se a Categoria está ativa.
     *
     * @return verdadeiro quando a Categoria está ativa.
     */
    public Boolean getAtivo() {
        return ativo;
    }

    /**
     * Define se a Categoria está ativa.
     *
     * @param ativo verdadeiro quando a Categoria está ativa.
     */
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * Retorna a Categoria pai.
     *
     * @return Categoria pai.
     */
    public Categoria getCategoriaPai() {
        return categoriaPai;
    }

    /**
     * Define a Categoria pai.
     *
     * @param categoriaPai Categoria pai.
     */
    public void setCategoriaPai(Categoria categoriaPai) {
        this.categoriaPai = categoriaPai;
    }

    /**
     * Retorna o usuário que criou a Categoria.
     *
     * @return usuário que criou a Categoria.
     */
    public String getCriadoPor() {
        return criadoPor;
    }

    /**
     * Define o usuário que criou a Categoria.
     *
     * @param criadoPor usuário que criou a Categoria.
     */
    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    /**
     * Retorna a data de criação da Categoria.
     *
     * @return data de criação da Categoria.
     */
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    /**
     * Define a data de criação da Categoria.
     *
     * @param criadoEm data de criação da Categoria.
     */
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    /**
     * Retorna o usuário que alterou a Categoria.
     *
     * @return usuário que alterou a Categoria.
     */
    public String getAlteradoPor() {
        return alteradoPor;
    }

    /**
     * Define o usuário que alterou a Categoria.
     *
     * @param alteradoPor usuário que alterou a Categoria.
     */
    public void setAlteradoPor(String alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    /**
     * Retorna a data de alteração da Categoria.
     *
     * @return data de alteração da Categoria.
     */
    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    /**
     * Define a data de alteração da Categoria.
     *
     * @param alteradoEm data de alteração da Categoria.
     */
    public void setAlteradoEm(LocalDateTime alteradoEm) {
        this.alteradoEm = alteradoEm;
    }

    /**
     * Compara Categoria por identificador.
     *
     * @param o objeto comparado.
     * @return verdadeiro quando os identificadores são iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categoria categoria)) {
            return false;
        }
        return Objects.equals(id, categoria.id);
    }

    /**
     * Calcula o hash da Categoria pelo identificador.
     *
     * @return hash da Categoria.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retorna uma representação textual da Categoria.
     *
     * @return representação textual da Categoria.
     */
    @Override
    public String toString() {
        return "Categoria{" +
            "id=" + id +
            ", nome='" + nome + '\'' +
            ", ativo=" + ativo +
            '}';
    }

}
