/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_CATEGORIAS } from '../config/api';
import { INIT_CONFIG, InitConfig } from '../config/init-config.token';
import { Categoria } from '../domain/entities/categoria';

export interface CategoriaFiltro {

  nome?: string;

  ativo?: boolean | null;

  categoriaPai?: number | null;

}

export const CATEGORIA_CAMPOS_PESQUISA: { campo: keyof CategoriaFiltro; label: string; tipo: string }[] = [
  { campo: 'nome', label: 'Nome', tipo: 'string' },
  { campo: 'ativo', label: 'Ativo?', tipo: 'boolean' },
  { campo: 'categoriaPai', label: 'Categoria Pai', tipo: 'fk' }
];

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly initConfig: InitConfig = inject(INIT_CONFIG);

  listar(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.initConfig.urlBackend}${API_CATEGORIAS}`);
  }

  pesquisar(filtro: CategoriaFiltro): Observable<Categoria[]> {
    let params = new HttpParams();

    if (filtro.nome != null && filtro.nome.trim() !== '') {
      params = params.set('nome', filtro.nome.trim());
    }

    if (filtro.ativo != null) {
      params = params.set('ativo', String(filtro.ativo));
    }

    if (filtro.categoriaPai != null) {
      params = params.set('categoriaPai', String(filtro.categoriaPai));
    }

    return this.http.get<Categoria[]>(`${this.initConfig.urlBackend}${API_CATEGORIAS}/pesquisar`, { params });
  }

  buscar(id: number): Observable<Categoria> {
    return this.http.get<Categoria>(`${this.initConfig.urlBackend}${API_CATEGORIAS}/${id}`);
  }

  incluir(categoria: Categoria): Observable<Categoria> {
    return this.http.post<Categoria>(`${this.initConfig.urlBackend}${API_CATEGORIAS}`, categoria);
  }

  alterar(categoria: Categoria, id: number): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.initConfig.urlBackend}${API_CATEGORIAS}/${id}`, categoria);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.initConfig.urlBackend}${API_CATEGORIAS}/${id}`);
  }

}
