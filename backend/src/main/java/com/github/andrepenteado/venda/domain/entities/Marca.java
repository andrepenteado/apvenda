/*
 * Autor: André Penteado
 * Criado em: 26/05/2026 17:21:01 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa uma Marca.
 */
@Entity
public class Marca implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é um campo obrigatório")
    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "criado_por", nullable = false)
    private String criadoPor;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "alterado_por")
    private String alteradoPor;

    @Column(name = "alterado_em")
    private LocalDateTime alteradoEm;

    /**
     * Retorna o identificador da Marca.
     *
     * @return identificador da Marca.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador da Marca.
     *
     * @param id identificador da Marca.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome da Marca.
     *
     * @return nome da Marca.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da Marca.
     *
     * @param nome nome da Marca.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o usuário que criou a Marca.
     *
     * @return usuário que criou a Marca.
     */
    public String getCriadoPor() {
        return criadoPor;
    }

    /**
     * Define o usuário que criou a Marca.
     *
     * @param criadoPor usuário que criou a Marca.
     */
    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    /**
     * Retorna a data de criação da Marca.
     *
     * @return data de criação da Marca.
     */
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    /**
     * Define a data de criação da Marca.
     *
     * @param criadoEm data de criação da Marca.
     */
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    /**
     * Retorna o usuário que alterou a Marca.
     *
     * @return usuário que alterou a Marca.
     */
    public String getAlteradoPor() {
        return alteradoPor;
    }

    /**
     * Define o usuário que alterou a Marca.
     *
     * @param alteradoPor usuário que alterou a Marca.
     */
    public void setAlteradoPor(String alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    /**
     * Retorna a data de alteração da Marca.
     *
     * @return data de alteração da Marca.
     */
    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    /**
     * Define a data de alteração da Marca.
     *
     * @param alteradoEm data de alteração da Marca.
     */
    public void setAlteradoEm(LocalDateTime alteradoEm) {
        this.alteradoEm = alteradoEm;
    }

    /**
     * Compara Marca por identificador.
     *
     * @param o objeto comparado.
     * @return verdadeiro quando os identificadores são iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Marca marca)) {
            return false;
        }
        return Objects.equals(id, marca.id);
    }

    /**
     * Calcula o hash da Marca pelo identificador.
     *
     * @return hash da Marca.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retorna uma representação textual da Marca.
     *
     * @return representação textual da Marca.
     */
    @Override
    public String toString() {
        return "Marca{" +
            "id=" + id +
            ", nome='" + nome + '\'' +
            '}';
    }

}
