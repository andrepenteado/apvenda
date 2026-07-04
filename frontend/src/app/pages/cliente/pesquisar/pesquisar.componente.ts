/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Datatables, DatatablesRequest, DatatablesResponse, DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { ConfigColumns } from 'datatables.net';
import { Observable, tap } from 'rxjs';
import Swal from 'sweetalert2';
import { Cliente } from '../../../domain/entities/cliente';
import { TipoPessoa, TipoPessoaLabels } from '../../../domain/enums/tipo-pessoa';
import { CLIENTE_CAMPOS_PESQUISA, ClienteFiltro, ClienteService } from '../../../services/cliente.service';
import { FiltroSessaoService } from '../../../services/filtro-sessao.service';

@Component({
  selector: 'venda-cliente-pesquisar',
  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './pesquisar.componente.html'
})
export class PesquisarComponente implements OnInit, OnDestroy {

  filtro: ClienteFiltro = {};
  totalListado = 0;
  readonly camposPesquisa = CLIENTE_CAMPOS_PESQUISA;

  private readonly tabelaId = '#datatables-pesquisar-clientes';
  private readonly filtroChave = 'cliente';
  private readonly service: ClienteService = inject(ClienteService);
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
    if (!this.temFiltroPreenchido()) {
      this.mensagemService.showMessage(
        'Informe ao menos um filtro para pesquisar.',
        'Clientes',
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

  novo(): void {
    this.router.navigate(['/clientes/cadastro']);
  }

  editar(id: number): void {
    this.router.navigate(['/clientes/cadastro', id]);
  }

  excluir(id: number): void {
    Swal.fire({
      title: 'Confirme',
      html: `Confirma a exclusão Cliente de ID #${id}`,
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
          this.mensagemService.showMessage('Cliente excluído com sucesso.', 'Clientes', DecoracaoMensagem.SUCESSO);
          this.recarregar();
        }
      });
    });
  }

  private temFiltroPreenchido(): boolean {
    return Boolean(
      (this.filtro.nome && this.filtro.nome.trim() !== '') ||
      (this.filtro.cpfCnpj && this.filtro.cpfCnpj.trim() !== '')
    );
  }

  /**
   * Consulta server-side do grid: o closure lê o filtro atual da tela a cada
   * draw, então pesquisar/limpar é apenas um reload do DataTables.
   */
  private consultar(request: DatatablesRequest): Observable<DatatablesResponse<Cliente>> {
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
            <span class="text-primary me-2"><i class="fa-solid fa-user"></i></span>
            <div>
              <div class="record-main fw-semibold">${nome}</div>
              <div class="record-sub text-secondary small">Cliente</div>
            </div>
          </div>`
      },
      {
        data: 'tipoPessoa',
        render: (tipoPessoa: TipoPessoa) => TipoPessoaLabels[tipoPessoa] ?? tipoPessoa
      },
      {
        data: 'cpfCnpj',
        render: (cpfCnpj: number, _tipo: unknown, cliente: Cliente) => this.formatarCpfCnpj(cliente)
      },
      {
        data: 'telefone',
        defaultContent: '-',
        render: (telefone: string | null) => telefone || '-'
      },
      {
        data: 'whatsapp',
        render: (whatsapp: boolean | null) => whatsapp ? 'Sim' : 'Não'
      }
    ];
  }

  private formatarCpfCnpj(cliente: Cliente): string {
    if (cliente.tipoPessoa === TipoPessoa.JURIDICA) {
      const digitos = String(cliente.cpfCnpj).padStart(14, '0');
      return digitos.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
    }

    const digitos = String(cliente.cpfCnpj).padStart(11, '0');
    return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

}
