/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { Datatables, DatatablesRequest, DatatablesResponse, DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { ConfigColumns } from 'datatables.net';
import { Observable, tap } from 'rxjs';
import Swal from 'sweetalert2';
import { Categoria } from '../../../domain/entities/categoria';
import { Marca } from '../../../domain/entities/marca';
import { Produto } from '../../../domain/entities/produto';
import { Unidade, UnidadeLabels } from '../../../domain/enums/unidade';
import { CategoriaService } from '../../../services/categoria.service';
import { FiltroSessaoService } from '../../../services/filtro-sessao.service';
import { MarcaService } from '../../../services/marca.service';
import { PRODUTO_CAMPOS_PESQUISA, ProdutoFiltro, ProdutoService } from '../../../services/produto.service';

const NUMERO = new Intl.NumberFormat('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const INTEIRO = new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 0 });

@Component({
  selector: 'venda-produto-pesquisar',
  imports: [
    CommonModule,
    FormsModule,
    NgSelectModule,
    RouterLink
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './pesquisar.componente.html'
})
export class PesquisarComponente implements OnInit, OnDestroy {

  categorias: Categoria[] = [];
  marcas: Marca[] = [];
  unidades = Object.entries(UnidadeLabels).map(([valor, label]) => ({ valor: valor as Unidade, label }));
  filtro: ProdutoFiltro = this.criarFiltro();
  totalListado = 0;
  readonly camposPesquisa = PRODUTO_CAMPOS_PESQUISA;

  private readonly tabelaId = '#datatables-pesquisar-produtos';
  private readonly filtroChave = 'produto';
  private readonly service: ProdutoService = inject(ProdutoService);
  private readonly categoriaService: CategoriaService = inject(CategoriaService);
  private readonly marcaService: MarcaService = inject(MarcaService);
  private readonly filtroSessao: FiltroSessaoService = inject(FiltroSessaoService);
  private readonly router: Router = inject(Router);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  ngOnInit(): void {
    // Restaura o filtro da sessão: o primeiro draw do grid já pesquisa com ele.
    this.filtro = this.filtroSessao.carregar(this.filtroChave, this.criarFiltro());
    this.listarCategorias();
    this.listarMarcas();
    this.inicializarDataTable();
  }

  ngOnDestroy(): void {
    this.destruirDataTable();
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

    this.filtroSessao.salvar(this.filtroChave, this.filtro);
    this.recarregar();
  }

  limparFiltros(): void {
    this.filtroSessao.limpar(this.filtroChave);
    this.filtro = this.criarFiltro();
    this.recarregar();
  }

  novo(): void {
    this.router.navigate(['/produtos/cadastro']);
  }

  editar(id: number): void {
    this.router.navigate(['/produtos/cadastro', id]);
  }

  excluir(id: number): void {
    Swal.fire({
      title: 'Confirme',
      html: `Confirma a exclusão Produto de ID #${id}`,
      icon: 'question',
      showCloseButton: true,
      showCancelButton: true,
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Cancelar',
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim'
    }).then(result => {
      if (!result.isConfirmed) {
        return;
      }

      this.service.excluir(id).subscribe({
        next: () => {
          this.mensagemService.showMessage('Produto excluído com sucesso.', 'Produtos', DecoracaoMensagem.SUCESSO);
          this.recarregar();
        }
      });
    });
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

  /**
   * Consulta server-side do grid: o closure lê o filtro atual da tela a cada
   * draw, então pesquisar/limpar é apenas um reload do DataTables.
   */
  private consultar(request: DatatablesRequest): Observable<DatatablesResponse<Produto>> {
    return this.service.datatables(request, this.filtro).pipe(
      tap(resposta => {
        this.totalListado = resposta.recordsFiltered;
        this.changeDetectorRef.detectChanges();
      })
    );
  }

  private inicializarDataTable(): void {
    setTimeout(() => {
      $(this.tabelaId).DataTable(Datatables.serverSide(request => this.consultar(request), this.colunas()));
      Datatables.aoClicarAcao(this.tabelaId, (acao, id) => {
        if (acao === 'editar') {
          this.editar(id);
        } else if (acao === 'excluir') {
          this.excluir(id);
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
            <button type="button" class="btn btn-outline-primary btn-sm" title="Editar" data-acao="editar" data-id="${id}">
              <i class="fa-solid fa-pen-to-square"></i>
            </button>
            <button type="button" class="btn btn-outline-danger btn-sm" title="Excluir" data-acao="excluir" data-id="${id}">
              <i class="fa-solid fa-trash-can"></i>
            </button>
          </div>`
      },
      { data: 'id' },
      {
        data: 'nome',
        render: (nome: string) => `
          <div class="record-cell d-flex align-items-center">
            <span class="text-primary me-2"><i class="fa-solid fa-box"></i></span>
            <div>
              <div class="record-main fw-semibold">${nome}</div>
              <div class="record-sub text-secondary small">Produto</div>
            </div>
          </div>`
      },
      { data: 'categoria.nome', defaultContent: '-' },
      { data: 'marca.nome', defaultContent: '-' },
      {
        data: 'precoVenda',
        className: 'text-end',
        render: (preco: number | null) => preco != null ? NUMERO.format(preco) : '-'
      },
      {
        data: 'estoqueAtual',
        className: 'text-end',
        render: (estoque: number | null) => estoque != null ? INTEIRO.format(estoque) : '-'
      },
      {
        data: 'ativo',
        render: (ativo: boolean) => `
          <span class="badge ${ativo ? 'bg-success' : 'bg-secondary'}">${ativo ? 'Sim' : 'Não'}</span>`
      }
    ];
  }

}
