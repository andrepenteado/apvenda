/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Datatables, DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import Swal from 'sweetalert2';
import { Cliente } from '../../../domain/entities/cliente';
import { TipoPessoa, TipoPessoaLabels } from '../../../domain/enums/tipo-pessoa';
import { CLIENTE_CAMPOS_PESQUISA, ClienteFiltro, ClienteService } from '../../../services/cliente.service';

@Component({
  selector: 'venda-cliente-pesquisar',
  imports: [
    CommonModule,
    FormsModule,
    NgxUiLoaderModule,
    RouterLink
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './pesquisar.componente.html'
})
export class PesquisarComponente implements OnInit, OnDestroy {

  clientes: Cliente[] = [];
  filtro: ClienteFiltro = {};
  exibirTabela = true;
  readonly camposPesquisa = CLIENTE_CAMPOS_PESQUISA;

  private readonly loaderId = 'cliente-pesquisar';
  private readonly tabelaId = '#datatables-pesquisar-clientes';
  private readonly service: ClienteService = inject(ClienteService);
  private readonly router: Router = inject(Router);
  private readonly changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  ngOnInit(): void {
    this.listar();
  }

  ngOnDestroy(): void {
    this.destruirDataTable();
  }

  listar(): void {
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.listar().subscribe({
      next: clientes => {
        this.atualizarGrid(clientes);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  pesquisar(): void {
    if ((!this.filtro.nome || this.filtro.nome.trim() === '') && (!this.filtro.cpfCnpj || this.filtro.cpfCnpj.trim() === '')) {
      this.mensagemService.showMessage(
        'Informe ao menos um filtro para pesquisar.',
        'Clientes',
        DecoracaoMensagem.ATENCAO
      );
      return;
    }

    this.uiLoaderService.startLoader(this.loaderId);
    this.service.pesquisar(this.filtro).subscribe({
      next: clientes => {
        this.atualizarGrid(clientes);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  limparFiltros(): void {
    this.filtro = {};
    this.listar();
  }

  novo(): void {
    this.router.navigate(['/clientes/cadastro']);
  }

  editar(cliente: Cliente): void {
    if (cliente.id != null) {
      this.router.navigate(['/clientes/cadastro', cliente.id]);
    }
  }

  excluir(cliente: Cliente): void {
    if (cliente.id == null) {
      return;
    }

    Swal.fire({
      title: 'Confirme',
      html: `Confirma a exclusão Cliente de ID #${cliente.id}`,
      icon: 'question',
      showCloseButton: true,
      showCancelButton: true,
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Cancelar',
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim'
    }).then(result => {
      if (!result.isConfirmed || cliente.id == null) {
        return;
      }

      this.uiLoaderService.startLoader(this.loaderId);
      this.service.excluir(cliente.id).subscribe({
        next: () => {
          this.mensagemService.showMessage('Cliente excluído com sucesso.', 'Clientes', DecoracaoMensagem.SUCESSO);
          this.listar();
        },
        error: () => this.uiLoaderService.stopLoader(this.loaderId)
      });
    });
  }

  formatarTipoPessoa(tipoPessoa: TipoPessoa): string {
    return TipoPessoaLabels[tipoPessoa];
  }

  formatarCpfCnpj(cliente: Cliente): string {
    if (cliente.tipoPessoa === TipoPessoa.JURIDICA) {
      const digitos = String(cliente.cpfCnpj).padStart(14, '0');
      return digitos.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
    }

    const digitos = String(cliente.cpfCnpj).padStart(11, '0');
    return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
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

  private atualizarGrid(clientes: Cliente[]): void {
    this.destruirDataTable();
    this.exibirTabela = false;
    this.clientes = [];
    this.changeDetectorRef.detectChanges();

    this.clientes = clientes;
    this.exibirTabela = true;
    this.changeDetectorRef.detectChanges();
    this.inicializarDataTable();
  }

}
