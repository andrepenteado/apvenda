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

export interface ItemVendaRequest {
  produto: number;
  quantidade: number;
}

export interface ParcelaRequest {
  formaPagamento: FormaPagamento;
  dataVencimento: string;
  valorPago: number | null;
}

export interface VendaRequest {
  itens: ItemVendaRequest[];
  cliente?: number | null;
  juros?: number;
  desconto?: number;
  parcelas?: ParcelaRequest[];
}

export interface ItemConsolidado {
  produto: number;
  nome: string;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
}

export interface VendaConsolidada {
  itens: ItemConsolidado[];
  total: number;
}

export interface ReceberResponse {
  parcela: number;
  dataVencimento: string;
  dataPagamento: string | null;
  formaPagamento: FormaPagamento;
  valorAReceber: number;
  valorPago: number | null;
}

export interface VendaResponse {
  id: number;
  dataHora: string;
  total: number;
  cliente: string | null;
  itens: ItemConsolidado[];
  recebimentos: ReceberResponse[];
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
  receberAberto: number;
  receberVencido: number;
  recebidoMes: number;
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

  preparar(request: VendaRequest): Observable<VendaConsolidada> {
    return this.http.post<VendaConsolidada>(`${this.initConfig.urlBackend}${API_VENDAS}/preparar`, request);
  }

  finalizar(request: VendaRequest): Observable<VendaResponse> {
    return this.http.post<VendaResponse>(`${this.initConfig.urlBackend}${API_VENDAS}`, request);
  }

  dashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.initConfig.urlBackend}${API_VENDAS}/dashboard`);
  }

}
