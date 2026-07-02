/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { Datatables, DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import Swal from 'sweetalert2';
import { Categoria } from '../../../domain/entities/categoria';
import { Marca } from '../../../domain/entities/marca';
import { Produto } from '../../../domain/entities/produto';
import { Unidade, UnidadeLabels } from '../../../domain/enums/unidade';
import { CategoriaService } from '../../../services/categoria.service';
import { MarcaService } from '../../../services/marca.service';
import { PRODUTO_CAMPOS_PESQUISA, ProdutoFiltro, ProdutoService } from '../../../services/produto.service';

@Component({
  selector: 'venda-produto-pesquisar',
  imports: [
    CommonModule,
    FormsModule,
    NgSelectModule,
    NgxUiLoaderModule,
    RouterLink
  ],
  templateUrl: './pesquisar.componente.html'
})
export class PesquisarComponente implements OnInit, OnDestroy {

  produtos: Produto[] = [];
  categorias: Categoria[] = [];
  marcas: Marca[] = [];
  unidades = Object.entries(UnidadeLabels).map(([valor, label]) => ({ valor: valor as Unidade, label }));
  filtro: ProdutoFiltro = this.criarFiltro();
  exibirTabela = true;
  readonly camposPesquisa = PRODUTO_CAMPOS_PESQUISA;

  private readonly loaderId = 'produto-pesquisar';
  private readonly tabelaId = '#datatables-pesquisar-produtos';
  private readonly service: ProdutoService = inject(ProdutoService);
  private readonly categoriaService: CategoriaService = inject(CategoriaService);
  private readonly marcaService: MarcaService = inject(MarcaService);
  private readonly router: Router = inject(Router);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  ngOnInit(): void {
    this.listarCategorias();
    this.listarMarcas();
    this.listar();
  }

  ngOnDestroy(): void {
    this.destruirDataTable();
  }

  listar(): void {
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.listar().subscribe({
      next: produtos => {
        this.atualizarGrid(produtos);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  pesquisar(): void {
    if (!this.temFiltroPreenchido()) {
      this.mensagemService.showMessage(
        'Informe ao menos um filtro para pesquisar.',
        'Produtos',
        DecoracaoMensagem.ATENCAO
      );
      return;
    }

    this.uiLoaderService.startLoader(this.loaderId);
    this.service.pesquisar(this.filtro).subscribe({
      next: produtos => {
        this.atualizarGrid(produtos);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  limparFiltros(): void {
    this.filtro = this.criarFiltro();
    this.listar();
  }

  novo(): void {
    this.router.navigate(['/produtos/cadastro']);
  }

  editar(produto: Produto): void {
    if (produto.id != null) {
      this.router.navigate(['/produtos/cadastro', produto.id]);
    }
  }

  excluir(produto: Produto): void {
    if (produto.id == null) {
      return;
    }

    Swal.fire({
      title: 'Confirme',
      html: `Confirma a exclusão Produto de ID #${produto.id}`,
      icon: 'question',
      showCloseButton: true,
      showCancelButton: true,
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Cancelar',
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim'
    }).then(result => {
      if (!result.isConfirmed || produto.id == null) {
        return;
      }

      this.uiLoaderService.startLoader(this.loaderId);
      this.service.excluir(produto.id).subscribe({
        next: () => {
          this.mensagemService.showMessage('Produto excluído com sucesso.', 'Produtos', DecoracaoMensagem.SUCESSO);
          this.listar();
        },
        error: () => this.uiLoaderService.stopLoader(this.loaderId)
      });
    });
  }

  getNomeCategoria(produto: Produto): string {
    return produto.categoria?.nome ?? '-';
  }

  getNomeMarca(produto: Produto): string {
    return produto.marca?.nome ?? '-';
  }

  getLabelUnidade(produto: Produto): string {
    return produto.unidade ? UnidadeLabels[produto.unidade] : '-';
  }

  private listarCategorias(): void {
    this.categoriaService.listar().subscribe({
      next: categorias => {
        this.categorias = categorias;
      }
    });
  }

  private listarMarcas(): void {
    this.marcaService.listar().subscribe({
      next: marcas => {
        this.marcas = marcas;
      }
    });
  }

  private temFiltroPreenchido(): boolean {
    return Boolean(
      (this.filtro.nome != null && this.filtro.nome.trim() !== '') ||
      (this.filtro.codigoBarras != null && this.filtro.codigoBarras.trim() !== '') ||
      this.filtro.categoria != null ||
      this.filtro.marca != null ||
      this.filtro.unidade != null ||
      this.filtro.ativo != null
    );
  }

  private criarFiltro(): ProdutoFiltro {
    return {
      ativo: null
    };
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

  private atualizarGrid(produtos: Produto[]): void {
    this.destruirDataTable();
    this.exibirTabela = false;
    this.produtos = [];
    this.changeDetectorRef.detectChanges();

    this.produtos = produtos;
    this.exibirTabela = true;
    this.changeDetectorRef.detectChanges();
    this.inicializarDataTable();
  }

}
