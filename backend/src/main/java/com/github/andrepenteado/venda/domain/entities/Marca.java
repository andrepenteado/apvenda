// André Penteado, 2026-05-06T00:00:00-03:00 - Entidade de Marca criada com ajuda da IA.
package com.github.andrepenteado.venda.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma Marca cadastrada no sistema.
 */
@Entity
public class Marca implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Descrição é um campo obrigatório")
    @Column(unique = true)
    private String descricao;

    private String criadoPor;

    private LocalDateTime criadoEm;

    private String alteradoPor;

    private LocalDateTime alteradoEm;

    /**
     * Retorna o identificador da Marca.
     *
     * @return identificador da Marca
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador da Marca.
     *
     * @param id identificador da Marca
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna a descrição da Marca.
     *
     * @return descrição da Marca
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define a descrição da Marca.
     *
     * @param descricao descrição da Marca
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna o usuário que criou a Marca.
     *
     * @return usuário que criou a Marca
     */
    public String getCriadoPor() {
        return criadoPor;
    }

    /**
     * Define o usuário que criou a Marca.
     *
     * @param criadoPor usuário que criou a Marca
     */
    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    /**
     * Retorna a data de criação da Marca.
     *
     * @return data de criação da Marca
     */
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    /**
     * Define a data de criação da Marca.
     *
     * @param criadoEm data de criação da Marca
     */
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    /**
     * Retorna o usuário da última alteração da Marca.
     *
     * @return usuário da última alteração da Marca
     */
    public String getAlteradoPor() {
        return alteradoPor;
    }

    /**
     * Define o usuário da última alteração da Marca.
     *
     * @param alteradoPor usuário da última alteração da Marca
     */
    public void setAlteradoPor(String alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    /**
     * Retorna a data da última alteração da Marca.
     *
     * @return data da última alteração da Marca
     */
    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    /**
     * Define a data da última alteração da Marca.
     *
     * @param alteradoEm data da última alteração da Marca
     */
    public void setAlteradoEm(LocalDateTime alteradoEm) {
        this.alteradoEm = alteradoEm;
    }

    /**
     * Compara a Marca pelo identificador.
     *
     * @param o objeto comparado
     * @return verdadeiro quando os identificadores forem iguais
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
     * Gera o hash da Marca pelo identificador.
     *
     * @return hash da Marca
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retorna a representação textual da Marca.
     *
     * @return representação textual da Marca
     */
    @Override
    public String toString() {
        return "Marca{" +
            "id=" + id +
            ", descricao='" + descricao + '\'' +
            ", criadoPor='" + criadoPor + '\'' +
            ", criadoEm=" + criadoEm +
            ", alteradoPor='" + alteradoPor + '\'' +
            ", alteradoEm=" + alteradoEm +
            '}';
    }

}
