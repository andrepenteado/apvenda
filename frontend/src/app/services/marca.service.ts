// André Penteado, 2026-05-06T00:00:00-03:00 - Service Angular de Marca criado com ajuda da IA.
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_MARCAS } from '../config/api';
import { INIT_CONFIG } from '../config/init-config.token';
import { Marca } from '../domain/entities/marca';

export const MARCA_CAMPOS_PESQUISA = [
  { campo: 'descricao', label: 'Descrição', tipo: 'contem' }
] as const;

@Injectable({
  providedIn: 'root'
})
export class MarcaService {

  private http = inject(HttpClient);
  private initConfig = inject(INIT_CONFIG);

  public listar(): Observable<Marca[]> {
    return this.http.get<Marca[]>(`${this.initConfig.urlBackend}${API_MARCAS}`);
  }

  public buscar(id: number): Observable<Marca> {
    return this.http.get<Marca>(`${this.initConfig.urlBackend}${API_MARCAS}/${id}`);
  }

  public pesquisar(campo: string, valor: string): Observable<Marca[]> {
    const params = new HttpParams().set('campo', campo).set('valor', valor);
    return this.http.get<Marca[]>(`${this.initConfig.urlBackend}${API_MARCAS}/pesquisar`, { params });
  }

  public incluir(marca: Marca): Observable<Marca> {
    return this.http.post<Marca>(`${this.initConfig.urlBackend}${API_MARCAS}`, marca);
  }

  public alterar(marca: Marca, id: number): Observable<Marca> {
    return this.http.put<Marca>(`${this.initConfig.urlBackend}${API_MARCAS}/${id}`, marca);
  }

  public excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.initConfig.urlBackend}${API_MARCAS}/${id}`);
  }

}
