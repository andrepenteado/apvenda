/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { Datatables, DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import Swal from 'sweetalert2';
import { Categoria } from '../../../domain/entities/categoria';
import { CATEGORIA_CAMPOS_PESQUISA, CategoriaFiltro, CategoriaService } from '../../../services/categoria.service';
import { FiltroSessaoService } from '../../../services/filtro-sessao.service';

@Component({
  selector: 'venda-categoria-pesquisar',
  imports: [
    CommonModule,
    FormsModule,
    NgSelectModule,
    NgxUiLoaderModule,
    RouterLink
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './pesquisar.componente.html'
})
export class PesquisarComponente implements OnInit, OnDestroy {

  categorias: Categoria[] = [];
  categoriasPai: Categoria[] = [];
  filtro: CategoriaFiltro = this.criarFiltro();
  exibirTabela = true;
  readonly camposPesquisa = CATEGORIA_CAMPOS_PESQUISA;

  private readonly loaderId = 'categoria-pesquisar';
  private readonly tabelaId = '#datatables-pesquisar-categorias';
  private readonly filtroChave = 'categoria';
  private readonly service: CategoriaService = inject(CategoriaService);
  private readonly filtroSessao: FiltroSessaoService = inject(FiltroSessaoService);
  private readonly router: Router = inject(Router);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  ngOnInit(): void {
    // Restaura o filtro da sessão e, quando preenchido, refaz a pesquisa com ele.
    this.filtro = this.filtroSessao.carregar(this.filtroChave, this.criarFiltro());
    this.listarCategoriasPai();
    if (this.temFiltroPreenchido()) {
      this.executarPesquisa();
    } else {
      this.listar();
    }
  }

  ngOnDestroy(): void {
    this.destruirDataTable();
  }

  listar(): void {
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.listar().subscribe({
      next: categorias => {
        this.atualizarGrid(categorias);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  pesquisar(): void {
    if (!this.temFiltroPreenchido()) {
      this.mensagemService.showMessage(
        'Informe ao menos um filtro para pesquisar.',
        'Categorias',
        DecoracaoMensagem.ATENCAO
      );
      return;
    }

    this.filtroSessao.salvar(this.filtroChave, this.filtro);
    this.executarPesquisa();
  }

  limparFiltros(): void {
    this.filtroSessao.limpar(this.filtroChave);
    this.filtro = this.criarFiltro();
    this.listar();
  }

  novo(): void {
    this.router.navigate(['/categorias/cadastro']);
  }

  editar(categoria: Categoria): void {
    if (categoria.id != null) {
      this.router.navigate(['/categorias/cadastro', categoria.id]);
    }
  }

  excluir(categoria: Categoria): void {
    if (categoria.id == null) {
      return;
    }

    Swal.fire({
      title: 'Confirme',
      html: `Confirma a exclusão Categoria de ID #${categoria.id}`,
      icon: 'question',
      showCloseButton: true,
      showCancelButton: true,
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Cancelar',
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim'
    }).then(result => {
      if (!result.isConfirmed || categoria.id == null) {
        return;
      }

      this.uiLoaderService.startLoader(this.loaderId);
      this.service.excluir(categoria.id).subscribe({
        next: () => {
          this.mensagemService.showMessage('Categoria excluída com sucesso.', 'Categorias', DecoracaoMensagem.SUCESSO);
          this.listar();
          this.listarCategoriasPai();
        },
        error: () => this.uiLoaderService.stopLoader(this.loaderId)
      });
    });
  }

  getNomeCategoriaPai(categoria: Categoria): string {
    return categoria.categoriaPai?.nome ?? '-';
  }

  private listarCategoriasPai(): void {
    this.service.listar().subscribe({
      next: categorias => {
        this.categoriasPai = categorias;
      }
    });
  }

  private temFiltroPreenchido(): boolean {
    return Boolean(
      (this.filtro.nome != null && this.filtro.nome.trim() !== '') ||
      this.filtro.ativo != null ||
      this.filtro.categoriaPai != null
    );
  }

  private criarFiltro(): CategoriaFiltro {
    return {
      ativo: null
    };
  }

  private executarPesquisa(): void {
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.pesquisar(this.filtro).subscribe({
      next: categorias => {
        this.atualizarGrid(categorias);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
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

  private atualizarGrid(categorias: Categoria[]): void {
    this.destruirDataTable();
    this.exibirTabela = false;
    this.categorias = [];
    this.changeDetectorRef.detectChanges();

    this.categorias = categorias;
    this.exibirTabela = true;
    this.changeDetectorRef.detectChanges();
    this.inicializarDataTable();
  }

}
