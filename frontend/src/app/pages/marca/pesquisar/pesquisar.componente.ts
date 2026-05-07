// André Penteado, 2026-05-06T00:00:00-03:00 - Componente de pesquisa de Marca criado com ajuda da IA.
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { Marca } from '../../../domain/entities/marca';
import { MARCA_CAMPOS_PESQUISA, MarcaService } from '../../../services/marca.service';
import { Datatables } from '@andre.penteado/ngx-apcore';

@Component({
  selector: 'venda-marca-pesquisar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, NgxUiLoaderModule],
  templateUrl: './pesquisar.componente.html',
  styles: [`
    .acoes { width: 90px; }
    .btn-icon { border: 0; background: transparent; padding: .25rem .4rem; }
  `]
})
export class PesquisarComponente implements OnInit {

  protected readonly camposPesquisa = MARCA_CAMPOS_PESQUISA;
  protected marcas: Marca[] = [];
  protected campoPesquisa = 'descricao';
  protected valorPesquisa = '';

  private marcaService = inject(MarcaService);
  private loader = inject(NgxUiLoaderService);
  private toastr = inject(ToastrService);
  private router = inject(Router);

  public ngOnInit(): void {
    this.listar();
  }

  protected listar(): void {
    this.loader.startLoader('marca-pesquisar');
    this.marcaService.listar()
      .pipe(finalize(() => this.loader.stopLoader('marca-pesquisar')))
      .subscribe({
        next: marcas => {
          this.marcas = marcas;
          setTimeout(() => {
            $('#datatables-pesquisar-marcas').DataTable(Datatables.config);
          }, 5);
        },
        error: () => this.toastr.error('Erro ao listar Marca.', 'Erro', { timeOut: 5000, progressBar: true, closeButton: true })
      });
  }

  protected pesquisar(): void {
    if (!this.valorPesquisa.trim()) {
      this.toastr.warning('Informe um valor para pesquisar.', 'Atenção', { timeOut: 3000, progressBar: true, closeButton: true });
      return;
    }

    this.loader.startLoader('marca-pesquisar');
    this.marcaService.pesquisar(this.campoPesquisa, this.valorPesquisa)
      .pipe(finalize(() => this.loader.stopLoader('marca-pesquisar')))
      .subscribe({
        next: marcas => this.marcas = marcas,
        error: () => this.toastr.error('Erro ao pesquisar Marca.', 'Erro', { timeOut: 5000, progressBar: true, closeButton: true })
      });
  }

  protected limparPesquisa(): void {
    this.valorPesquisa = '';
    this.campoPesquisa = 'descricao';
    this.listar();
  }

  protected editar(marca: Marca): void {
    if (marca.id !== undefined) {
      this.router.navigate(['/marca/cadastro', marca.id]);
    }
  }

  protected excluir(marca: Marca): void {
    if (marca.id === undefined) {
      return;
    }

    this.loader.startLoader('marca-pesquisar');
    this.marcaService.excluir(marca.id)
      .pipe(finalize(() => this.loader.stopLoader('marca-pesquisar')))
      .subscribe({
        next: () => {
          this.toastr.success('Marca excluída com sucesso!', 'Sucesso', { timeOut: 3000, progressBar: true, closeButton: true });
          this.listar();
        },
        error: () => this.toastr.error('Erro ao excluir Marca. Tente novamente.', 'Erro', { timeOut: 5000, progressBar: true, closeButton: true })
      });
  }

}
