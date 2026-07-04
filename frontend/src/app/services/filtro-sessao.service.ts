/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { Injectable } from '@angular/core';

/**
 * Guarda os filtros das telas de pesquisa na sessão do navegador: o usuário
 * filtra, navega para outra tela (ou para o cadastro do CRUD) e, ao voltar,
 * encontra o filtro como deixou. O filtro só é descartado quando o usuário
 * limpa/muda ou quando a aba é fechada (sessionStorage).
 */
@Injectable({
  providedIn: 'root'
})
export class FiltroSessaoService {

  private readonly prefixo = 'apvenda:filtro:';

  /**
   * Carrega o filtro salvo da tela, ou o padrão quando não há filtro salvo.
   *
   * @param chave identificador da tela (ex.: "produto").
   * @param padrao filtro padrão da tela.
   */
  carregar<T extends object>(chave: string, padrao: T): T {
    const json = sessionStorage.getItem(this.prefixo + chave);
    if (!json) {
      return padrao;
    }
    try {
      return { ...padrao, ...JSON.parse(json) };
    } catch {
      return padrao;
    }
  }

  /**
   * Salva o filtro aplicado pela tela.
   *
   * @param chave identificador da tela.
   * @param filtro filtro aplicado.
   */
  salvar<T extends object>(chave: string, filtro: T): void {
    sessionStorage.setItem(this.prefixo + chave, JSON.stringify(filtro));
  }

  /**
   * Descarta o filtro salvo da tela.
   *
   * @param chave identificador da tela.
   */
  limpar(chave: string): void {
    sessionStorage.removeItem(this.prefixo + chave);
  }

}
