/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { Datatables, DatatablesRequest, DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { ConfigColumns } from 'datatables.net';
import { Observable, tap } from 'rxjs';
import Swal from 'sweetalert2';
import { FormaPagamento, FormaPagamentoLabels } from '../../../domain/enums/forma-pagamento';
import { FiltroSessaoService } from '../../../services/filtro-sessao.service';
import { VendaDatatablesResponse, VendaFiltro, VendaResponse, VendaService } from '../../../services/venda.service';
import { ImprimirVendaComponente } from '../imprimir/imprimir.componente';

const NUMERO = new Intl.NumberFormat('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const DATA_HORA = new Intl.DateTimeFormat('pt-BR', {
  day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
});

@Component({
  selector: 'venda-pesquisar',
  imports: [
    CommonModule,
    FormsModule,
    NgxMaskDirective,
    RouterLink,
    ImprimirVendaComponente
  ],
  providers: [provideNgxMask()],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './pesquisar.componente.html'
})
export class PesquisarComponente implements OnInit, OnDestroy {

  filtro: VendaFiltro = {};
  totalListado = 0;
  valorTotalListado = 0;
  valorPagoListado = 0;

  impressaoAberta = false;
  vendaImpressao?: VendaResponse;

  private readonly tabelaId = '#datatables-pesquisar-venda';
  private readonly filtroChave = 'venda';
  private readonly service: VendaService = inject(VendaService);
  private readonly filtroSessao: FiltroSessaoService = inject(FiltroSessaoService);
  private readonly router: Router = inject(Router);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  ngOnInit(): void {
    // Restaura o filtro da sessão: o primeiro draw do grid já pesquisa com ele.
    this.filtro = this.filtroSessao.carregar(this.filtroChave, {});
    this.inicializarDataTable();
  }

  ngOnDestroy(): void {
    this.destruirDataTable();
  }

  pesquisar(): void {
    if (this.filtroVazio()) {
      this.mensagemService.showMessage(
        'Informe ao menos um filtro para pesquisar.',
        'Vendas',
        DecoracaoMensagem.ATENCAO
      );
      return;
    }

    this.filtroSessao.salvar(this.filtroChave, this.filtro);
    this.recarregar();
  }

  limparFiltros(): void {
    this.filtroSessao.limpar(this.filtroChave);
    this.filtro = {};
    this.recarregar();
  }

  consultar(id: number): void {
    this.router.navigate(['/vendas/consultar', id]);
  }

  imprimir(id: number): void {
    this.service.buscar(id).subscribe({
      next: venda => {
        this.vendaImpressao = venda;
        this.impressaoAberta = true;
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  fecharImpressao(): void {
    this.impressaoAberta = false;
  }

  estornar(id: number): void {
    Swal.fire({
      title: 'Estornar venda',
      html: `Estornar a <b>Venda #${id}</b>?<br>A venda, os itens e o financeiro serão excluídos e o estoque será devolvido.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim, estornar',
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Não',
      focusCancel: true
    }).then(resultado => {
      if (!resultado.isConfirmed) {
        return;
      }
      this.service.estornar(id).subscribe({
        next: () => {
          this.mensagemService.showMessage(
            `Venda #${id} estornada com sucesso.`,
            'Vendas',
            DecoracaoMensagem.SUCESSO
          );
          this.recarregar();
        }
      });
    });
  }

  private filtroVazio(): boolean {
    return (!this.filtro.idVenda || String(this.filtro.idVenda).trim() === '')
      && !this.filtro.dataInicio
      && !this.filtro.dataFinal
      && (!this.filtro.cpfCliente || this.filtro.cpfCliente.trim() === '')
      && this.filtro.consumidor !== true;
  }

  /**
   * Consulta server-side do grid: o closure lê o filtro atual da tela a cada
   * draw; os agregados dos cards de resumo vêm calculados do backend sobre o
   * resultado filtrado inteiro (não apenas a página).
   */
  private consultarGrid(request: DatatablesRequest): Observable<VendaDatatablesResponse> {
    return this.service.datatables(request, this.filtro).pipe(
      tap(resposta => {
        this.totalListado = resposta.recordsFiltered;
        this.valorTotalListado = resposta.valorTotalGeral;
        this.valorPagoListado = resposta.valorPagoGeral;
        this.changeDetectorRef.detectChanges();
      })
    );
  }

  private inicializarDataTable(): void {
    setTimeout(() => {
      $(this.tabelaId).DataTable(Datatables.serverSide(request => this.consultarGrid(request), this.colunas()));
      Datatables.aoClicarAcao(this.tabelaId, (acao, id) => {
        if (acao === 'consultar') {
          this.consultar(id);
        } else if (acao === 'imprimir') {
          this.imprimir(id);
        } else if (acao === 'estornar') {
          this.estornar(id);
        }
      });
    }, 5);
  }

  private destruirDataTable(): void {
    if ($.fn.dataTable.isDataTable(this.tabelaId)) {
      $(this.tabelaId).DataTable().destroy();
    }
  }

  private recarregar(): void {
    if ($.fn.dataTable.isDataTable(this.tabelaId)) {
      $(this.tabelaId).DataTable().ajax.reload();
    }
  }

  private colunas(): ConfigColumns[] {
    return [
      {
        data: 'id',
        orderable: false,
        searchable: false,
        render: (id: number) => `
          <div class="action-stack d-flex gap-1">
            <button type="button" class="btn btn-outline-primary btn-sm" title="Consultar venda" data-acao="consultar" data-id="${id}">
              <i class="fa-solid fa-eye"></i>
            </button>
            <button type="button" class="btn btn-outline-secondary btn-sm" title="Imprimir venda" data-acao="imprimir" data-id="${id}">
              <i class="fa-solid fa-print"></i>
            </button>
            <button type="button" class="btn btn-outline-danger btn-sm" title="Estornar venda" data-acao="estornar" data-id="${id}">
              <i class="fa-solid fa-rotate-left"></i>
            </button>
          </div>`
      },
      { data: 'id' },
      {
        data: 'dataHora',
        render: (dataHora: string) => dataHora ? DATA_HORA.format(new Date(dataHora)) : '-'
      },
      {
        data: 'nomeCliente',
        defaultContent: '',
        render: (nomeCliente: string | null, _tipo: unknown, venda: { id: number }) => `
          <div class="record-cell d-flex align-items-center">
            <span class="text-primary me-2"><i class="fa-solid fa-file-invoice-dollar"></i></span>
            <div>
              <div class="record-main fw-semibold">${nomeCliente || 'Consumidor'}</div>
              <div class="record-sub text-secondary small">Venda #${venda.id}</div>
            </div>
          </div>`
      },
      {
        data: 'cpfCliente',
        defaultContent: '-',
        render: (cpf: number | null) => this.formatarCpf(cpf)
      },
      {
        data: 'formaPagamento',
        defaultContent: '-',
        orderable: false,
        render: (formaPagamento: FormaPagamento | null) =>
          formaPagamento ? (FormaPagamentoLabels[formaPagamento] ?? formaPagamento) : '-'
      },
      {
        data: 'total',
        className: 'text-end',
        render: (total: number) => NUMERO.format(total)
      },
      {
        data: 'valorPago',
        className: 'text-end',
        orderable: false,
        render: (valorPago: number | null) => valorPago != null ? NUMERO.format(valorPago) : '-'
      }
    ];
  }

  private formatarCpf(cpf: number | null | undefined): string {
    if (cpf == null) {
      return '-';
    }
    const digitos = String(cpf).padStart(11, '0');
    return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

}
