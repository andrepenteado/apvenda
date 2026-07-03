/*
 * Autor: André Penteado
 * Criado em: 03/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { Datatables, DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import Swal from 'sweetalert2';
import { FormaPagamento, FormaPagamentoLabels } from '../../../domain/enums/forma-pagamento';
import { VendaFiltro, VendaPesquisa, VendaService } from '../../../services/venda.service';

@Component({
  selector: 'venda-pesquisar',
  imports: [
    CommonModule,
    FormsModule,
    NgxMaskDirective,
    NgxUiLoaderModule,
    RouterLink
  ],
  providers: [provideNgxMask()],
  templateUrl: './pesquisar.componente.html'
})
export class PesquisarComponente implements OnInit, OnDestroy {

  vendas: VendaPesquisa[] = [];
  filtro: VendaFiltro = {};
  exibirTabela = true;

  private readonly loaderId = 'venda-pesquisar';
  private readonly tabelaId = '#datatables-pesquisar-venda';
  private readonly service: VendaService = inject(VendaService);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  get valorTotalListado(): number {
    return this.vendas.reduce((soma, venda) => soma + Number(venda.total ?? 0), 0);
  }

  get valorPagoListado(): number {
    return this.vendas.reduce((soma, venda) => soma + Number(venda.valorPago ?? 0), 0);
  }

  ngOnInit(): void {
    this.listar();
  }

  ngOnDestroy(): void {
    this.destruirDataTable();
  }

  listar(): void {
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.listar().subscribe({
      next: vendas => {
        this.atualizarGrid(vendas);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
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

    this.executarPesquisa();
  }

  limparFiltros(): void {
    this.filtro = {};
    this.listar();
  }

  estornar(venda: VendaPesquisa): void {
    Swal.fire({
      title: 'Estornar venda',
      html: `Estornar a <b>Venda #${venda.id}</b>?<br>A venda, os itens e o financeiro serão excluídos e o estoque será devolvido.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim, estornar',
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Não',
      focusCancel: true
    }).then(resultado => {
      if (!resultado.isConfirmed) {
        return;
      }
      this.uiLoaderService.startLoader(this.loaderId);
      this.service.estornar(venda.id).subscribe({
        next: () => {
          this.mensagemService.showMessage(
            `Venda #${venda.id} estornada com sucesso.`,
            'Vendas',
            DecoracaoMensagem.SUCESSO
          );
          this.recarregar();
        },
        error: () => this.uiLoaderService.stopLoader(this.loaderId)
      });
    });
  }

  formatarCpf(cpf: number | null | undefined): string {
    if (cpf == null) {
      return '-';
    }
    const digitos = String(cpf).padStart(11, '0');
    return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  formaPagamentoLabel(formaPagamento: FormaPagamento | null): string {
    return formaPagamento ? (FormaPagamentoLabels[formaPagamento] ?? formaPagamento) : '-';
  }

  private filtroVazio(): boolean {
    return (!this.filtro.idVenda || String(this.filtro.idVenda).trim() === '')
      && !this.filtro.dataInicio
      && !this.filtro.dataFinal
      && (!this.filtro.cpfCliente || this.filtro.cpfCliente.trim() === '')
      && this.filtro.consumidor !== true;
  }

  private executarPesquisa(): void {
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.pesquisar(this.filtro).subscribe({
      next: vendas => {
        this.atualizarGrid(vendas);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  private recarregar(): void {
    if (this.filtroVazio()) {
      this.listar();
    } else {
      this.executarPesquisa();
    }
  }

  private inicializarDataTable(): void {
    setTimeout(() => {
      $(this.tabelaId).DataTable(Datatables.config);
      this.uiLoaderService.stopLoader(this.loaderId);
    }, 5);
  }

  private destruirDataTable(): void {
    if ($.fn.dataTable.isDataTable(this.tabelaId)) {
      $(this.tabelaId).DataTable().destroy();
    }
  }

  private atualizarGrid(vendas: VendaPesquisa[]): void {
    this.destruirDataTable();
    this.exibirTabela = false;
    this.vendas = [];
    this.changeDetectorRef.detectChanges();

    this.vendas = vendas;
    this.exibirTabela = true;
    this.changeDetectorRef.detectChanges();
    this.inicializarDataTable();
  }

}
