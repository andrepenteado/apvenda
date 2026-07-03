/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_CLIENTES } from '../config/api';
import { INIT_CONFIG, InitConfig } from '../config/init-config.token';
import { Cliente } from '../domain/entities/cliente';

export interface ClienteFiltro {

  nome?: string;

  cpf?: string;

}

export const CLIENTE_CAMPOS_PESQUISA: { campo: keyof ClienteFiltro; label: string; tipo: string }[] = [
  { campo: 'nome', label: 'Nome', tipo: 'string' },
  { campo: 'cpf', label: 'CPF', tipo: 'long' }
];

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly initConfig: InitConfig = inject(INIT_CONFIG);

  listar(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.initConfig.urlBackend}${API_CLIENTES}`);
  }

  pesquisar(filtro: ClienteFiltro): Observable<Cliente[]> {
    let params = new HttpParams();

    if (filtro.nome != null && filtro.nome.trim() !== '') {
      params = params.set('nome', filtro.nome.trim());
    }

    if (filtro.cpf != null && filtro.cpf.trim() !== '') {
      params = params.set('cpf', filtro.cpf.trim());
    }

    return this.http.get<Cliente[]>(`${this.initConfig.urlBackend}${API_CLIENTES}/pesquisar`, { params });
  }

  buscar(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.initConfig.urlBackend}${API_CLIENTES}/${id}`);
  }

  incluir(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.initConfig.urlBackend}${API_CLIENTES}`, cliente);
  }

  alterar(cliente: Cliente, id: number): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.initConfig.urlBackend}${API_CLIENTES}/${id}`, cliente);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.initConfig.urlBackend}${API_CLIENTES}/${id}`);
  }

}
