/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_PRODUTOS, API_VENDAS } from '../config/api';
import { INIT_CONFIG, InitConfig } from '../config/init-config.token';
import { Produto } from '../domain/entities/produto';
import { FormaPagamento } from '../domain/enums/forma-pagamento';
import { Unidade } from '../domain/enums/unidade';

export interface ItemVendaRequest {
  produto: number;
  quantidade: number;
}

export interface VendaRequest {
  itens: ItemVendaRequest[];
  cliente?: number | null;
  juros?: number;
  desconto?: number;
  formaPagamento?: FormaPagamento;
}

export interface ItemConsolidado {
  produto: number;
  nome: string;
  unidade: Unidade;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
}

export interface VendaResponse {
  id: number;
  dataHora: string;
  total: number;
  cliente: string | null;
  telefoneCliente: string | null;
  itens: ItemConsolidado[];
  formaPagamento: FormaPagamento;
  valorPago: number;
}

export interface VendaFiltro {

  idVenda?: string;

  dataInicio?: string;

  dataFinal?: string;

  cpfCliente?: string;

  consumidor?: boolean;

}

export interface VendaPesquisa {
  id: number;
  dataHora: string;
  nomeCliente: string | null;
  cpfCliente: number | null;
  formaPagamento: FormaPagamento | null;
  total: number;
  valorPago: number | null;
}

export interface VendaDia {
  data: string;
  total: number;
  quantidade: number;
}

export interface TopProduto {
  nome: string;
  quantidade: number;
  total: number;
}

export interface FormaPagamentoTotal {
  formaPagamento: FormaPagamento;
  total: number;
}

export interface TopCliente {
  nome: string;
  quantidade: number;
  total: number;
}

export interface DashboardResponse {
  totalHoje: number;
  quantidadeHoje: number;
  totalMes: number;
  quantidadeMes: number;
  ticketMedioMes: number;
  vendasPorDia: VendaDia[];
  topProdutos: TopProduto[];
  formasPagamento: FormaPagamentoTotal[];
  topClientes: TopCliente[];
}

@Injectable({
  providedIn: 'root'
})
export class VendaService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly initConfig: InitConfig = inject(INIT_CONFIG);

  buscarProduto(termo: string): Observable<Produto[]> {
    const params = new HttpParams().set('termo', termo);
    return this.http.get<Produto[]>(`${this.initConfig.urlBackend}${API_PRODUTOS}/pdv`, { params });
  }

  finalizar(request: VendaRequest): Observable<VendaResponse> {
    return this.http.post<VendaResponse>(`${this.initConfig.urlBackend}${API_VENDAS}`, request);
  }

  listar(): Observable<VendaPesquisa[]> {
    return this.http.get<VendaPesquisa[]>(`${this.initConfig.urlBackend}${API_VENDAS}`);
  }

  pesquisar(filtro: VendaFiltro): Observable<VendaPesquisa[]> {
    let params = new HttpParams();

    if (filtro.idVenda != null && String(filtro.idVenda).trim() !== '') {
      params = params.set('idVenda', String(filtro.idVenda).trim());
    }

    if (filtro.dataInicio != null && filtro.dataInicio !== '') {
      params = params.set('dataInicio', filtro.dataInicio);
    }

    if (filtro.dataFinal != null && filtro.dataFinal !== '') {
      params = params.set('dataFinal', filtro.dataFinal);
    }

    if (filtro.cpfCliente != null && filtro.cpfCliente.trim() !== '') {
      params = params.set('cpfCliente', filtro.cpfCliente.trim());
    }

    if (filtro.consumidor === true) {
      params = params.set('consumidor', 'true');
    }

    return this.http.get<VendaPesquisa[]>(`${this.initConfig.urlBackend}${API_VENDAS}/pesquisar`, { params });
  }

  buscar(id: number): Observable<VendaResponse> {
    return this.http.get<VendaResponse>(`${this.initConfig.urlBackend}${API_VENDAS}/${id}`);
  }

  estornar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.initConfig.urlBackend}${API_VENDAS}/${id}`);
  }

  dashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.initConfig.urlBackend}${API_VENDAS}/dashboard`);
  }

}
