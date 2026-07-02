/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
package com.github.andrepenteado.venda.domain.enums;

/**
 * Unidades de medida e de grandeza usadas no cadastro de produtos.
 * O nome da constante é o valor gravado no banco (ex.: PACOTE, METRO).
 */
public enum Unidade {

    UNIDADE("Unidade"),
    PECA("Peça"),
    PACOTE("Pacote"),
    CAIXA("Caixa"),
    FARDO("Fardo"),
    DUZIA("Dúzia"),
    PAR("Par"),
    KIT("Kit"),
    CONJUNTO("Conjunto"),
    JOGO("Jogo"),
    ROLO("Rolo"),
    SACO("Saco"),
    LATA("Lata"),
    GALAO("Galão"),
    FRASCO("Frasco"),
    TUBO("Tubo"),
    BARRA("Barra"),
    CHAPA("Chapa"),
    METRO("Metro"),
    METRO_QUADRADO("Metro quadrado"),
    METRO_CUBICO("Metro cúbico"),
    CENTIMETRO("Centímetro"),
    MILIMETRO("Milímetro"),
    QUILOGRAMA("Quilograma"),
    GRAMA("Grama"),
    TONELADA("Tonelada"),
    LITRO("Litro"),
    MILILITRO("Mililitro");

    private final String descricao;

    Unidade(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição apresentável da unidade.
     *
     * @return descrição da unidade.
     */
    public String getDescricao() {
        return descricao;
    }

}
