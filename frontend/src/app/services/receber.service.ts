/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_RECEBER } from '../config/api';
import { INIT_CONFIG, InitConfig } from '../config/init-config.token';
import { Receber } from '../domain/entities/receber';
import { FormaPagamento } from '../domain/enums/forma-pagamento';

export interface ReceberFiltro {

  idVenda?: string;

  nomeCliente?: string;

  cpfCliente?: string;

  dataInicio?: string;

  dataFinal?: string;

  emAberto?: boolean;

}

export interface ReceberBaixaRequest {

  idsParcelas: number[];

  dataPagamento: string;

  formaPagamento: FormaPagamento;

  valorPago: number;

}

export const RECEBER_CAMPOS_PESQUISA: { campo: keyof ReceberFiltro; label: string; tipo: string }[] = [
  { campo: 'idVenda', label: 'Venda', tipo: 'long' },
  { campo: 'nomeCliente', label: 'Nome do cliente', tipo: 'string' },
  { campo: 'cpfCliente', label: 'CPF do cliente', tipo: 'long' },
  { campo: 'dataInicio', label: 'Data início', tipo: 'date' },
  { campo: 'dataFinal', label: 'Data final', tipo: 'date' },
  { campo: 'emAberto', label: 'Em aberto?', tipo: 'boolean' }
];

@Injectable({
  providedIn: 'root'
})
export class ReceberService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly initConfig: InitConfig = inject(INIT_CONFIG);

  listar(): Observable<Receber[]> {
    return this.http.get<Receber[]>(`${this.initConfig.urlBackend}${API_RECEBER}`);
  }

  pesquisar(filtro: ReceberFiltro): Observable<Receber[]> {
    let params = new HttpParams();

    if (filtro.idVenda != null && String(filtro.idVenda).trim() !== '') {
      params = params.set('idVenda', String(filtro.idVenda).trim());
    }

    if (filtro.nomeCliente != null && filtro.nomeCliente.trim() !== '') {
      params = params.set('nomeCliente', filtro.nomeCliente.trim());
    }

    if (filtro.cpfCliente != null && filtro.cpfCliente.trim() !== '') {
      params = params.set('cpfCliente', filtro.cpfCliente.trim());
    }

    if (filtro.dataInicio != null && filtro.dataInicio !== '') {
      params = params.set('dataInicio', filtro.dataInicio);
    }

    if (filtro.dataFinal != null && filtro.dataFinal !== '') {
      params = params.set('dataFinal', filtro.dataFinal);
    }

    if (filtro.emAberto === true) {
      params = params.set('emAberto', 'true');
    }

    return this.http.get<Receber[]>(`${this.initConfig.urlBackend}${API_RECEBER}/pesquisar`, { params });
  }

  baixar(request: ReceberBaixaRequest): Observable<Receber> {
    return this.http.post<Receber>(`${this.initConfig.urlBackend}${API_RECEBER}/baixa`, request);
  }

}
