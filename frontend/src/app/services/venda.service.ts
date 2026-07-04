/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { DatatablesRequest, DatatablesResponse } from '@andre.penteado/ngx-apcore';
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

/**
 * Resposta server-side do grid de vendas: página do protocolo DataTables mais
 * os agregados do resultado filtrado inteiro (cards de resumo da tela).
 */
export interface VendaDatatablesResponse extends DatatablesResponse<VendaPesquisa> {
  valorTotalGeral: number;
  valorPagoGeral: number;
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

  datatables(datatables: DatatablesRequest, filtro: VendaFiltro): Observable<VendaDatatablesResponse> {
    return this.http.post<VendaDatatablesResponse>(
      `${this.initConfig.urlBackend}${API_VENDAS}/datatables`,
      { datatables, filtro: this.normalizarFiltro(filtro) }
    );
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

  /**
   * Normaliza o filtro antes do envio: strings vazias viram null para o
   * backend não receber valores em branco.
   */
  private normalizarFiltro(filtro: VendaFiltro): VendaFiltro {
    return {
      idVenda: filtro.idVenda != null && String(filtro.idVenda).trim() !== '' ? String(filtro.idVenda).trim() : undefined,
      dataInicio: filtro.dataInicio || undefined,
      dataFinal: filtro.dataFinal || undefined,
      cpfCliente: filtro.cpfCliente != null && filtro.cpfCliente.trim() !== '' ? filtro.cpfCliente.trim() : undefined,
      consumidor: filtro.consumidor === true ? true : undefined
    };
  }

}
