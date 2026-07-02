/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_PRODUTOS } from '../config/api';
import { INIT_CONFIG, InitConfig } from '../config/init-config.token';
import { Produto } from '../domain/entities/produto';
import { Unidade, UnidadeLabels } from '../domain/enums/unidade';

export interface ProdutoFiltro {

  nome?: string;

  codigoBarras?: string;

  categoria?: number | null;

  marca?: number | null;

  unidade?: Unidade | null;

  ativo?: boolean | null;

}

export const PRODUTO_CAMPOS_PESQUISA: { campo: keyof ProdutoFiltro; label: string; tipo: string; enumLabels?: Record<string, string> }[] = [
  { campo: 'nome', label: 'Nome', tipo: 'string' },
  { campo: 'codigoBarras', label: 'Código de Barras', tipo: 'string' },
  { campo: 'categoria', label: 'Categoria', tipo: 'fk' },
  { campo: 'marca', label: 'Marca', tipo: 'fk' },
  { campo: 'unidade', label: 'Unidade', tipo: 'enum', enumLabels: UnidadeLabels },
  { campo: 'ativo', label: 'Ativo?', tipo: 'boolean' }
];

@Injectable({
  providedIn: 'root'
})
export class ProdutoService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly initConfig: InitConfig = inject(INIT_CONFIG);

  listar(): Observable<Produto[]> {
    return this.http.get<Produto[]>(`${this.initConfig.urlBackend}${API_PRODUTOS}`);
  }

  pesquisar(filtro: ProdutoFiltro): Observable<Produto[]> {
    let params = new HttpParams();

    if (filtro.nome != null && filtro.nome.trim() !== '') {
      params = params.set('nome', filtro.nome.trim());
    }

    if (filtro.codigoBarras != null && filtro.codigoBarras.trim() !== '') {
      params = params.set('codigoBarras', filtro.codigoBarras.trim());
    }

    if (filtro.categoria != null) {
      params = params.set('categoria', String(filtro.categoria));
    }

    if (filtro.marca != null) {
      params = params.set('marca', String(filtro.marca));
    }

    if (filtro.unidade != null) {
      params = params.set('unidade', String(filtro.unidade));
    }

    if (filtro.ativo != null) {
      params = params.set('ativo', String(filtro.ativo));
    }

    return this.http.get<Produto[]>(`${this.initConfig.urlBackend}${API_PRODUTOS}/pesquisar`, { params });
  }

  buscar(id: number): Observable<Produto> {
    return this.http.get<Produto>(`${this.initConfig.urlBackend}${API_PRODUTOS}/${id}`);
  }

  incluir(produto: Produto): Observable<Produto> {
    return this.http.post<Produto>(`${this.initConfig.urlBackend}${API_PRODUTOS}`, produto);
  }

  alterar(produto: Produto, id: number): Observable<Produto> {
    return this.http.put<Produto>(`${this.initConfig.urlBackend}${API_PRODUTOS}/${id}`, produto);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.initConfig.urlBackend}${API_PRODUTOS}/${id}`);
  }

}
