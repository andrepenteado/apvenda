/*
 * Autor: André Penteado
 * Criado em: 26/05/2026 17:21:01 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Datatables, DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import Swal from 'sweetalert2';
import { Marca } from '../../../domain/entities/marca';
import { MARCA_CAMPOS_PESQUISA, MarcaFiltro, MarcaService } from '../../../services/marca.service';

@Component({
  selector: 'venda-marca-pesquisar',
  imports: [
    CommonModule,
    FormsModule,
    NgxUiLoaderModule,
    RouterLink
  ],
  templateUrl: './pesquisar.componente.html'
})
export class PesquisarComponente implements OnInit, OnDestroy {

  marcas: Marca[] = [];
  filtro: MarcaFiltro = {};
  exibirTabela = true;
  readonly camposPesquisa = MARCA_CAMPOS_PESQUISA;

  private readonly loaderId = 'marca-pesquisar';
  private readonly tabelaId = '#datatables-pesquisar-marcas';
  private readonly service: MarcaService = inject(MarcaService);
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
      next: marcas => {
        this.atualizarGrid(marcas);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  pesquisar(): void {
    if (!this.filtro.nome || this.filtro.nome.trim() === '') {
      this.mensagemService.showMessage(
        'Informe ao menos um filtro para pesquisar.',
        'Marcas',
        DecoracaoMensagem.ATENCAO
      );
      return;
    }

    this.uiLoaderService.startLoader(this.loaderId);
    this.service.pesquisar(this.filtro).subscribe({
      next: marcas => {
        this.atualizarGrid(marcas);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  limparFiltros(): void {
    this.filtro = {};
    this.listar();
  }

  novo(): void {
    this.router.navigate(['/marcas/cadastro']);
  }

  editar(marca: Marca): void {
    if (marca.id != null) {
      this.router.navigate(['/marcas/cadastro', marca.id]);
    }
  }

  excluir(marca: Marca): void {
    if (marca.id == null) {
      return;
    }

    Swal.fire({
      title: 'Confirme',
      html: `Confirma a exclusão Marca de ID #${marca.id}`,
      icon: 'question',
      showCloseButton: true,
      showCancelButton: true,
      cancelButtonText: '<i class="fa-solid fa-x fa-lg"></i> Cancelar',
      confirmButtonText: '<i class="fa-solid fa-check fa-lg"></i> Sim'
    }).then(result => {
      if (!result.isConfirmed || marca.id == null) {
        return;
      }

      this.uiLoaderService.startLoader(this.loaderId);
      this.service.excluir(marca.id).subscribe({
        next: () => {
          this.mensagemService.showMessage('Marca excluída com sucesso.', 'Marcas', DecoracaoMensagem.SUCESSO);
          this.listar();
        },
        error: () => this.uiLoaderService.stopLoader(this.loaderId)
      });
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

  private atualizarGrid(marcas: Marca[]): void {
    this.destruirDataTable();
    this.exibirTabela = false;
    this.marcas = [];
    this.changeDetectorRef.detectChanges();

    this.marcas = marcas;
    this.exibirTabela = true;
    this.changeDetectorRef.detectChanges();
    this.inicializarDataTable();
  }

}
