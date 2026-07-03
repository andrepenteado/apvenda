/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgSelectComponent, NgSelectModule } from '@ng-select/ng-select';
import { DecoracaoMensagem, ExibirMensagemService, UploadService } from '@andre.penteado/ngx-apcore';
import { Subject, Subscription, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import Swal from 'sweetalert2';
import { Cliente } from '../../domain/entities/cliente';
import { Produto } from '../../domain/entities/produto';
import { FormaPagamento, FormaPagamentoLabels } from '../../domain/enums/forma-pagamento';
import { Unidade, UnidadeLabels } from '../../domain/enums/unidade';
import { ClienteService } from '../../services/cliente.service';
import { ParcelaRequest, VendaRequest, VendaResponse, VendaService } from '../../services/venda.service';

interface ItemCarrinho {
  produtoId: number;
  nome: string;
  unidade: Unidade;
  foto?: string | null;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
}

interface LinhaParcela {
  formaPagamento: FormaPagamento;
  dataVencimento: string;
  valorParcela: number;
  valorPago: number | null;
}

@Component({
  selector: 'venda-pdv',
  imports: [CommonModule, FormsModule, NgSelectModule],
  templateUrl: './pdv.componente.html',
  styleUrl: './pdv.componente.css'
})
export class PdvComponente implements OnInit, OnDestroy {

  itens: ItemCarrinho[] = [];
  produtos: Produto[] = [];
  readonly pesquisa$ = new Subject<string>();
  buscando = false;
  produtoModel: Produto | null = null;
  produtoSelecionado: Produto | null = null;
  quantidade = 1;

  readonly semImagem = 'assets/images/sem-imagem.gif';

  modoExclusao = false;
  indiceExclusao = 0;

  clienteModalAberto = false;
  clienteSelecionado: Cliente | null = null;
  clienteModel: Cliente | null = null;
  clientes: Cliente[] = [];
  readonly pesquisaCliente$ = new Subject<string>();
  buscandoCliente = false;

  pagamentoAberto = false;
  totalConsolidado = 0;
  numParcelas = 1;
  juros = 0;
  desconto = 0;
  formaPagamento: FormaPagamento = FormaPagamento.DINHEIRO;
  dataPrimeiraParcela = this.hojeIso();
  linhasParcela: LinhaParcela[] = [];

  impressaoAberta = false;
  vendaFinalizada: VendaResponse | null = null;

  readonly formasPagamento = Object.entries(FormaPagamentoLabels).map(([valor, label]) => ({ valor: valor as FormaPagamento, label }));
  readonly opcoesParcelas = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

  @ViewChild('pesquisaSelect') pesquisaSelect?: NgSelectComponent;
  @ViewChild('inputQtd') inputQtd?: ElementRef<HTMLInputElement>;
  @ViewChild('clienteSelect') clienteSelect?: NgSelectComponent;
  @ViewChild('selectForma') selectForma?: ElementRef<HTMLSelectElement>;

  private readonly service = inject(VendaService);
  private readonly clienteService = inject(ClienteService);
  private readonly uploadService = inject(UploadService);
  private readonly mensagemService = inject(ExibirMensagemService);
  private subscription?: Subscription;
  private subscriptionCliente?: Subscription;
  private readonly fotosCache = new Map<string, string>();

  ngOnInit(): void {
    this.subscription = this.pesquisa$.pipe(
      debounceTime(250),
      distinctUntilChanged(),
      switchMap(termo => {
        this.buscando = true;
        return this.service.buscarProduto(termo ?? '');
      })
    ).subscribe({
      next: produtos => {
        this.produtos = produtos;
        this.buscando = false;
        this.carregarFotos(produtos);
      },
      error: () => (this.buscando = false)
    });
    this.subscriptionCliente = this.pesquisaCliente$.pipe(
      debounceTime(250),
      distinctUntilChanged(),
      switchMap(termo => {
        this.buscandoCliente = true;
        return this.clienteService.pesquisar({ nome: termo ?? '' });
      })
    ).subscribe({
      next: clientes => {
        this.clientes = clientes;
        this.buscandoCliente = false;
      },
      error: () => (this.buscandoCliente = false)
    });
    setTimeout(() => this.focarPesquisa(), 100);
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    this.subscriptionCliente?.unsubscribe();
  }

  get total(): number {
    return this.arred2(this.itens.reduce((soma, item) => soma + item.valorTotal, 0));
  }

  get liquido(): number {
    return this.arred2((this.totalConsolidado * (100 + (this.juros || 0) - (this.desconto || 0))) / 100);
  }

  get cabecalhoCupom(): string {
    return this.vendaFinalizada ? `VENDA #${this.vendaFinalizada.id}` : 'ORÇAMENTO';
  }

  get nomeCliente(): string {
    return this.clienteSelecionado?.nome ?? 'CONSUMIDOR';
  }

  fotoSrc(entidade: { foto?: string | null } | null): string {
    const uuid = entidade?.foto;
    return (uuid && this.fotosCache.get(uuid)) || this.semImagem;
  }

  labelParcelas(n: number): string {
    return n === 1 ? 'À vista' : `${n}x`;
  }

  labelForma(forma: FormaPagamento): string {
    return FormaPagamentoLabels[forma];
  }

  unidadeLabel(unidade?: Unidade | null): string {
    return unidade ? UnidadeLabels[unidade] : '';
  }

  selecionarProduto(produto: Produto | null): void {
    if (!produto) {
      this.produtoSelecionado = null;
      return;
    }
    this.produtoSelecionado = produto;
    this.quantidade = 1;
    setTimeout(() => this.focarQuantidade(), 0);
  }

  adicionarAoCarrinho(): void {
    if (!this.produtoSelecionado) {
      return;
    }
    if (!this.quantidade || this.quantidade <= 0) {
      this.aviso('Informe uma quantidade válida.');
      return;
    }
    const produto = this.produtoSelecionado;
    const preco = produto.precoVenda ?? 0;
    const existente = this.itens.find(item => item.produtoId === produto.id);
    if (existente) {
      existente.quantidade = this.arred2(existente.quantidade + this.quantidade);
      existente.valorTotal = this.arred2(existente.quantidade * existente.valorUnitario);
    } else {
      this.itens.push({
        produtoId: produto.id!,
        nome: produto.nome,
        unidade: produto.unidade,
        foto: produto.foto,
        quantidade: this.quantidade,
        valorUnitario: preco,
        valorTotal: this.arred2(this.quantidade * preco)
      });
    }
    this.produtoSelecionado = null;
    this.produtoModel = null;
    this.produtos = [];
    this.quantidade = 1;
    setTimeout(() => this.focarPesquisa(), 0);
  }

  abrirModalCliente(): void {
    this.clienteModel = this.clienteSelecionado;
    this.clientes = this.clienteSelecionado ? [this.clienteSelecionado] : [];
    this.clienteModalAberto = true;
    setTimeout(() => this.clienteSelect?.focus(), 100);
  }

  fecharModalCliente(): void {
    this.clienteModalAberto = false;
    setTimeout(() => this.focarPesquisa(), 0);
  }

  selecionarCliente(cliente: Cliente | null): void {
    this.clienteSelecionado = cliente ?? null;
    this.fecharModalCliente();
  }

  removerVinculoCliente(): void {
    this.clienteSelecionado = null;
    this.clienteModel = null;
    this.fecharModalCliente();
  }

  formatarCpf(cpf: number): string {
    const digitos = String(cpf).padStart(11, '0');
    return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  confirmarExclusao(): void {
    const item = this.itens[this.indiceExclusao];
    if (!item) {
      this.modoExclusao = false;
      return;
    }
    Swal.fire({
      title: 'Confirme',
      html: `Excluir <b>${item.nome}</b> do carrinho?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim',
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Não',
      focusCancel: true
    }).then(resultado => {
      if (resultado.isConfirmed) {
        this.itens.splice(this.indiceExclusao, 1);
      }
      this.modoExclusao = false;
      setTimeout(() => this.focarPesquisa(), 0);
    });
  }

  realizarPagamento(): void {
    if (this.itens.length === 0) {
      this.aviso('Adicione ao menos um produto para receber o pagamento.');
      return;
    }
    const request: VendaRequest = {
      itens: this.itens.map(item => ({ produto: item.produtoId, quantidade: item.quantidade }))
    };
    this.service.preparar(request).subscribe({
      next: consolidada => {
        this.totalConsolidado = consolidada.total;
        this.numParcelas = 1;
        this.juros = 0;
        this.desconto = 0;
        this.formaPagamento = FormaPagamento.DINHEIRO;
        this.dataPrimeiraParcela = this.hojeIso();
        this.montarLinhasParcela();
        this.pagamentoAberto = true;
        setTimeout(() => this.selectForma?.nativeElement.focus(), 100);
      }
    });
  }

  montarLinhasParcela(): void {
    const n = this.numParcelas;
    const liq = this.liquido;
    const base = Math.floor((liq / n) * 100) / 100;
    const resto = this.arred2(liq - base * n);
    const linhas: LinhaParcela[] = [];
    for (let i = 0; i < n; i++) {
      const valor = i === 0 ? this.arred2(base + resto) : base;
      linhas.push({
        formaPagamento: this.formaPagamento,
        dataVencimento: this.addDiasIso(this.dataPrimeiraParcela, i * 30),
        valorParcela: valor,
        valorPago: valor
      });
    }
    this.linhasParcela = linhas;
  }

  finalizarVenda(): void {
    const parcelas: ParcelaRequest[] = this.linhasParcela.map(linha => ({
      formaPagamento: linha.formaPagamento,
      dataVencimento: linha.dataVencimento,
      valorPago: linha.valorPago === null || linha.valorPago === undefined || (linha.valorPago as unknown) === '' ? null : linha.valorPago
    }));
    const request: VendaRequest = {
      itens: this.itens.map(item => ({ produto: item.produtoId, quantidade: item.quantidade })),
      cliente: this.clienteSelecionado?.id ?? null,
      juros: this.juros || 0,
      desconto: this.desconto || 0,
      parcelas
    };
    this.service.finalizar(request).subscribe({
      next: venda => {
        this.vendaFinalizada = venda;
        this.pagamentoAberto = false;
        this.mensagemService.showMessage(`Venda #${venda.id} finalizada com sucesso.`, 'PDV', DecoracaoMensagem.SUCESSO);
        this.impressaoAberta = true;
      }
    });
  }

  voltarVenda(): void {
    this.pagamentoAberto = false;
    setTimeout(() => this.focarPesquisa(), 0);
  }

  cancelarVenda(): void {
    if (this.itens.length === 0 && !this.pagamentoAberto) {
      return;
    }
    Swal.fire({
      title: 'Cancelar venda',
      html: 'Deseja cancelar a venda atual? Os itens serão descartados.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim, cancelar',
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Não',
      focusCancel: true
    }).then(resultado => {
      if (resultado.isConfirmed) {
        this.novaVenda();
      }
    });
  }

  abrirImpressao(): void {
    if (this.itens.length === 0) {
      this.aviso('Nada para imprimir.');
      return;
    }
    this.impressaoAberta = true;
  }

  fecharImpressao(): void {
    const finalizou = this.vendaFinalizada != null;
    this.impressaoAberta = false;
    if (finalizou) {
      this.novaVenda();
    } else {
      setTimeout(() => this.focarPesquisa(), 0);
    }
  }

  imprimir(): void {
    window.print();
  }

  novaVenda(): void {
    this.itens = [];
    this.produtoSelecionado = null;
    this.produtoModel = null;
    this.produtos = [];
    this.quantidade = 1;
    this.modoExclusao = false;
    this.clienteModalAberto = false;
    this.clienteSelecionado = null;
    this.clienteModel = null;
    this.clientes = [];
    this.pagamentoAberto = false;
    this.impressaoAberta = false;
    this.vendaFinalizada = null;
    this.numParcelas = 1;
    this.juros = 0;
    this.desconto = 0;
    this.formaPagamento = FormaPagamento.DINHEIRO;
    this.dataPrimeiraParcela = this.hojeIso();
    this.linhasParcela = [];
    setTimeout(() => this.focarPesquisa(), 0);
  }

  @HostListener('window:keydown', ['$event'])
  handleKey(evento: KeyboardEvent): void {
    const tecla = evento.key;

    // Cancelar: a qualquer momento.
    if (evento.ctrlKey && (tecla === 'c' || tecla === 'C')) {
      evento.preventDefault();
      this.cancelarVenda();
      return;
    }

    if (this.impressaoAberta) {
      if (tecla === 'Escape') {
        evento.preventDefault();
        this.fecharImpressao();
      }
      // Ctrl+P é tratado nativamente pelo navegador (imprime só o modal via CSS).
      return;
    }

    if (this.pagamentoAberto) {
      if (evento.ctrlKey && tecla === 'F11') {
        evento.preventDefault();
        this.finalizarVenda();
      } else if (tecla === 'Escape') {
        evento.preventDefault();
        this.voltarVenda();
      }
      return;
    }

    if (this.clienteModalAberto) {
      if (tecla === 'Escape') {
        evento.preventDefault();
        this.fecharModalCliente();
      }
      return;
    }

    // Montagem da venda.
    if (tecla === 'F2') {
      evento.preventDefault();
      this.focarPesquisa();
      return;
    }
    if (tecla === 'F3') {
      evento.preventDefault();
      this.focarQuantidade();
      return;
    }
    if (tecla === 'F4') {
      evento.preventDefault();
      this.abrirImpressao();
      return;
    }
    if (tecla === 'F6') {
      evento.preventDefault();
      this.abrirModalCliente();
      return;
    }
    if (tecla === 'F10') {
      evento.preventDefault();
      this.realizarPagamento();
      return;
    }
    if (this.modoExclusao) {
      if (tecla === 'ArrowUp') {
        evento.preventDefault();
        // No primeiro item, seta para cima não faz nada.
        if (this.indiceExclusao > 0) {
          this.indiceExclusao--;
        }
      } else if (tecla === 'ArrowDown') {
        evento.preventDefault();
        // No último item, seta para baixo não faz nada.
        if (this.indiceExclusao < this.itens.length - 1) {
          this.indiceExclusao++;
        }
      } else if (tecla === 'Enter') {
        evento.preventDefault();
        this.confirmarExclusao();
      } else if (tecla === 'Escape') {
        evento.preventDefault();
        this.modoExclusao = false;
      }
      return;
    }
    if (tecla === 'Delete' && this.itens.length > 0) {
      evento.preventDefault();
      this.modoExclusao = true;
      this.indiceExclusao = 0;
    }
  }

  private carregarFotos(produtos: Produto[]): void {
    for (const produto of produtos) {
      const uuid = produto.foto;
      if (!uuid || this.fotosCache.has(uuid)) {
        continue;
      }
      // Reserva a entrada no cache para não disparar buscas duplicadas em paralelo.
      this.fotosCache.set(uuid, this.semImagem);
      this.uploadService.buscar(uuid).subscribe({
        next: upload => this.fotosCache.set(uuid, `data:${upload.tipoMime};base64,${upload.base64}`)
      });
    }
  }

  private focarPesquisa(): void {
    this.pesquisaSelect?.focus();
  }

  private focarQuantidade(): void {
    const elemento = this.inputQtd?.nativeElement;
    if (elemento) {
      elemento.focus();
      elemento.select();
    }
  }

  private aviso(mensagem: string): void {
    this.mensagemService.showMessage(mensagem, 'PDV', DecoracaoMensagem.ATENCAO);
  }

  private hojeIso(): string {
    return new Date().toISOString().slice(0, 10);
  }

  private addDiasIso(iso: string, dias: number): string {
    const data = new Date(iso + 'T00:00:00');
    data.setDate(data.getDate() + dias);
    return data.toISOString().slice(0, 10);
  }

  private arred2(valor: number): number {
    return Math.round((valor + Number.EPSILON) * 100) / 100;
  }

}
