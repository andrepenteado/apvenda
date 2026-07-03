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
import { FormaPagamento, FormaPagamentoLabels } from '../../../domain/enums/forma-pagamento';
import { Receber } from '../../../domain/entities/receber';
import { RECEBER_CAMPOS_PESQUISA, ReceberFiltro, ReceberService } from '../../../services/receber.service';

interface BaixaModel {
  dataPagamento: string;
  formaPagamento: FormaPagamento;
  valorPago: number | null;
}

@Component({
  selector: 'venda-receber-pesquisar',
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

  parcelas: Receber[] = [];
  filtro: ReceberFiltro = {};
  exibirTabela = true;
  selecionadas = new Set<number>();
  modalBaixaAberto = false;
  baixa: BaixaModel = { dataPagamento: '', formaPagamento: FormaPagamento.DINHEIRO, valorPago: null };
  readonly camposPesquisa = RECEBER_CAMPOS_PESQUISA;
  readonly formasPagamento = Object.values(FormaPagamento).map(valor => ({ valor, label: FormaPagamentoLabels[valor] }));

  private readonly loaderId = 'receber-pesquisar';
  private readonly tabelaId = '#datatables-pesquisar-receber';
  private readonly service: ReceberService = inject(ReceberService);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  get parcelasSelecionadas(): Receber[] {
    return this.parcelas.filter(parcela => parcela.id != null && this.selecionadas.has(parcela.id));
  }

  get totalSelecionado(): number {
    return this.parcelasSelecionadas.reduce((soma, parcela) => soma + Number(parcela.valorAReceber ?? 0), 0);
  }

  get vendaSelecionada(): number | null {
    const vendas = [...new Set(this.parcelasSelecionadas.map(parcela => parcela.idVenda))];
    return vendas.length === 1 ? vendas[0] : null;
  }

  get totalEmAberto(): number {
    return this.parcelas.filter(parcela => parcela.valorPago == null).length;
  }

  get valorEmAberto(): number {
    return this.parcelas
      .filter(parcela => parcela.valorPago == null)
      .reduce((soma, parcela) => soma + Number(parcela.valorAReceber ?? 0), 0);
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
      next: parcelas => {
        this.atualizarGrid(parcelas);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  pesquisar(): void {
    if (this.filtroVazio()) {
      this.mensagemService.showMessage(
        'Informe ao menos um filtro para pesquisar.',
        'Contas a Receber',
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

  alternarSelecao(parcela: Receber): void {
    if (parcela.id == null) {
      return;
    }

    if (this.selecionadas.has(parcela.id)) {
      this.selecionadas.delete(parcela.id);
    } else {
      this.selecionadas.add(parcela.id);
    }
  }

  estaSelecionada(parcela: Receber): boolean {
    return parcela.id != null && this.selecionadas.has(parcela.id);
  }

  abrirModalBaixa(): void {
    if (this.selecionadas.size === 0) {
      this.mensagemService.showMessage(
        'Selecione ao menos uma parcela em aberto para dar baixa.',
        'Contas a Receber',
        DecoracaoMensagem.ATENCAO
      );
      return;
    }

    if (this.vendaSelecionada == null) {
      this.mensagemService.showMessage(
        'Só é permitido dar baixa em lote de parcelas da mesma venda.',
        'Contas a Receber',
        DecoracaoMensagem.ATENCAO
      );
      return;
    }

    this.baixa = {
      dataPagamento: this.dataCorrente(),
      formaPagamento: FormaPagamento.DINHEIRO,
      valorPago: Number(this.totalSelecionado.toFixed(2))
    };
    this.modalBaixaAberto = true;
  }

  fecharModalBaixa(): void {
    this.modalBaixaAberto = false;
  }

  confirmarBaixa(): void {
    if (!this.baixa.dataPagamento || !this.baixa.formaPagamento || this.baixa.valorPago == null || this.baixa.valorPago <= 0) {
      this.mensagemService.showMessage(
        'Informe data de pagamento, forma de pagamento e valor pago maior que zero.',
        'Contas a Receber',
        DecoracaoMensagem.ATENCAO
      );
      return;
    }

    this.uiLoaderService.startLoader(this.loaderId);
    this.service.baixar({
      idsParcelas: [...this.selecionadas],
      dataPagamento: this.baixa.dataPagamento,
      formaPagamento: this.baixa.formaPagamento,
      valorPago: this.baixa.valorPago
    }).subscribe({
      next: () => {
        this.mensagemService.showMessage(
          'Baixa das parcelas realizada com sucesso.',
          'Contas a Receber',
          DecoracaoMensagem.SUCESSO
        );
        this.modalBaixaAberto = false;
        this.recarregar();
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  formatarCpf(cpf: number | null | undefined): string {
    if (cpf == null) {
      return '-';
    }
    const digitos = String(cpf).padStart(11, '0');
    return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  formaPagamentoLabel(formaPagamento: FormaPagamento): string {
    return FormaPagamentoLabels[formaPagamento] ?? formaPagamento;
  }

  descreverParcela(parcela: Receber): string {
    return parcela.parcela === 0 ? 'À vista' : `Parcela ${parcela.parcela}`;
  }

  private filtroVazio(): boolean {
    return (!this.filtro.idVenda || String(this.filtro.idVenda).trim() === '')
      && (!this.filtro.nomeCliente || this.filtro.nomeCliente.trim() === '')
      && (!this.filtro.cpfCliente || this.filtro.cpfCliente.trim() === '')
      && !this.filtro.dataInicio
      && !this.filtro.dataFinal
      && this.filtro.emAberto !== true;
  }

  private executarPesquisa(): void {
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.pesquisar(this.filtro).subscribe({
      next: parcelas => {
        this.atualizarGrid(parcelas);
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

  private dataCorrente(): string {
    const hoje = new Date();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const dia = String(hoje.getDate()).padStart(2, '0');
    return `${hoje.getFullYear()}-${mes}-${dia}`;
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

  private atualizarGrid(parcelas: Receber[]): void {
    this.selecionadas.clear();
    this.destruirDataTable();
    this.exibirTabela = false;
    this.parcelas = [];
    this.changeDetectorRef.detectChanges();

    this.parcelas = parcelas;
    this.exibirTabela = true;
    this.changeDetectorRef.detectChanges();
    this.inicializarDataTable();
  }

}
