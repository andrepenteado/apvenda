/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa um Cliente.
 */
@Entity
public class Cliente implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é um campo obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "CPF é um campo obrigatório")
    @Column(nullable = false, unique = true)
    private Long cpf;

    @Column
    private String telefone;

    @Column
    private Boolean whatsapp;

    @Column(name = "criado_por", nullable = false)
    private String criadoPor;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "alterado_por")
    private String alteradoPor;

    @Column(name = "alterado_em")
    private LocalDateTime alteradoEm;

    /**
     * Retorna o identificador do Cliente.
     *
     * @return identificador do Cliente.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador do Cliente.
     *
     * @param id identificador do Cliente.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome do Cliente.
     *
     * @return nome do Cliente.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do Cliente.
     *
     * @param nome nome do Cliente.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o CPF do Cliente.
     *
     * @return CPF do Cliente.
     */
    public Long getCpf() {
        return cpf;
    }

    /**
     * Define o CPF do Cliente.
     *
     * @param cpf CPF do Cliente.
     */
    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    /**
     * Retorna o telefone do Cliente.
     *
     * @return telefone do Cliente.
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Define o telefone do Cliente.
     *
     * @param telefone telefone do Cliente.
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * Retorna se o telefone do Cliente é Whatsapp.
     *
     * @return verdadeiro quando o telefone do Cliente é Whatsapp.
     */
    public Boolean getWhatsapp() {
        return whatsapp;
    }

    /**
     * Define se o telefone do Cliente é Whatsapp.
     *
     * @param whatsapp verdadeiro quando o telefone do Cliente é Whatsapp.
     */
    public void setWhatsapp(Boolean whatsapp) {
        this.whatsapp = whatsapp;
    }

    /**
     * Retorna o usuário que criou o Cliente.
     *
     * @return usuário que criou o Cliente.
     */
    public String getCriadoPor() {
        return criadoPor;
    }

    /**
     * Define o usuário que criou o Cliente.
     *
     * @param criadoPor usuário que criou o Cliente.
     */
    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    /**
     * Retorna a data de criação do Cliente.
     *
     * @return data de criação do Cliente.
     */
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    /**
     * Define a data de criação do Cliente.
     *
     * @param criadoEm data de criação do Cliente.
     */
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    /**
     * Retorna o usuário que alterou o Cliente.
     *
     * @return usuário que alterou o Cliente.
     */
    public String getAlteradoPor() {
        return alteradoPor;
    }

    /**
     * Define o usuário que alterou o Cliente.
     *
     * @param alteradoPor usuário que alterou o Cliente.
     */
    public void setAlteradoPor(String alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    /**
     * Retorna a data de alteração do Cliente.
     *
     * @return data de alteração do Cliente.
     */
    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    /**
     * Define a data de alteração do Cliente.
     *
     * @param alteradoEm data de alteração do Cliente.
     */
    public void setAlteradoEm(LocalDateTime alteradoEm) {
        this.alteradoEm = alteradoEm;
    }

    /**
     * Compara Cliente por identificador.
     *
     * @param o objeto comparado.
     * @return verdadeiro quando os identificadores são iguais.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente cliente)) {
            return false;
        }
        return Objects.equals(id, cliente.id);
    }

    /**
     * Calcula o hash do Cliente pelo identificador.
     *
     * @return hash do Cliente.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retorna uma representação textual do Cliente.
     *
     * @return representação textual do Cliente.
     */
    @Override
    public String toString() {
        return "Cliente{" +
            "id=" + id +
            ", nome='" + nome + '\'' +
            ", cpf=" + cpf +
            '}';
    }

}
