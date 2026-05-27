/*
 * Autor: André Penteado
 * Criado em: 26/05/2026 17:21:01 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_MARCAS } from '../config/api';
import { INIT_CONFIG, InitConfig } from '../config/init-config.token';
import { Marca } from '../domain/entities/marca';

export interface MarcaFiltro {

  nome?: string;

}

export const MARCA_CAMPOS_PESQUISA: { campo: keyof MarcaFiltro; label: string; tipo: string }[] = [
  { campo: 'nome', label: 'Nome', tipo: 'string' }
];

@Injectable({
  providedIn: 'root'
})
export class MarcaService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly initConfig: InitConfig = inject(INIT_CONFIG);

  listar(): Observable<Marca[]> {
    return this.http.get<Marca[]>(`${this.initConfig.urlBackend}${API_MARCAS}`);
  }

  pesquisar(filtro: MarcaFiltro): Observable<Marca[]> {
    let params = new HttpParams();

    if (filtro.nome != null && filtro.nome.trim() !== '') {
      params = params.set('nome', filtro.nome.trim());
    }

    return this.http.get<Marca[]>(`${this.initConfig.urlBackend}${API_MARCAS}/pesquisar`, { params });
  }

  buscar(id: number): Observable<Marca> {
    return this.http.get<Marca>(`${this.initConfig.urlBackend}${API_MARCAS}/${id}`);
  }

  incluir(marca: Marca): Observable<Marca> {
    return this.http.post<Marca>(`${this.initConfig.urlBackend}${API_MARCAS}`, marca);
  }

  alterar(marca: Marca, id: number): Observable<Marca> {
    return this.http.put<Marca>(`${this.initConfig.urlBackend}${API_MARCAS}/${id}`, marca);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.initConfig.urlBackend}${API_MARCAS}/${id}`);
  }

}
