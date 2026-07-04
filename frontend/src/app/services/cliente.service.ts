/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { DatatablesRequest, DatatablesResponse } from '@andre.penteado/ngx-apcore';
import { Observable } from 'rxjs';
import { API_CLIENTES } from '../config/api';
import { INIT_CONFIG, InitConfig } from '../config/init-config.token';
import { Cliente } from '../domain/entities/cliente';

export interface ClienteFiltro {

  nome?: string;

  cpfCnpj?: string;

}

export const CLIENTE_CAMPOS_PESQUISA: { campo: keyof ClienteFiltro; label: string; tipo: string }[] = [
  { campo: 'nome', label: 'Nome', tipo: 'string' },
  { campo: 'cpfCnpj', label: 'CPF/CNPJ', tipo: 'long' }
];

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly initConfig: InitConfig = inject(INIT_CONFIG);

  datatables(datatables: DatatablesRequest, filtro: ClienteFiltro): Observable<DatatablesResponse<Cliente>> {
    // Strings vazias viram undefined para o backend não receber valores em branco.
    const filtroNormalizado: ClienteFiltro = {
      nome: filtro.nome != null && filtro.nome.trim() !== '' ? filtro.nome.trim() : undefined,
      cpfCnpj: filtro.cpfCnpj != null && filtro.cpfCnpj.trim() !== '' ? filtro.cpfCnpj.trim() : undefined
    };
    return this.http.post<DatatablesResponse<Cliente>>(
      `${this.initConfig.urlBackend}${API_CLIENTES}/datatables`,
      { datatables, filtro: filtroNormalizado }
    );
  }

  pesquisar(filtro: ClienteFiltro): Observable<Cliente[]> {
    let params = new HttpParams();

    if (filtro.nome != null && filtro.nome.trim() !== '') {
      params = params.set('nome', filtro.nome.trim());
    }

    if (filtro.cpfCnpj != null && filtro.cpfCnpj.trim() !== '') {
      params = params.set('cpfCnpj', filtro.cpfCnpj.trim());
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
