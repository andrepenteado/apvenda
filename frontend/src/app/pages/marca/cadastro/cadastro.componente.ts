// André Penteado, 2026-05-06T00:00:00-03:00 - Componente de cadastro de Marca criado com ajuda da IA.
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { Marca } from '../../../domain/entities/marca';
import { MarcaService } from '../../../services/marca.service';

@Component({
  selector: 'venda-marca-cadastro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, NgxUiLoaderModule],
  templateUrl: './cadastro.componente.html',
  styles: [`
    .auditoria { font-size: .875rem; }
  `]
})
export class CadastroComponente implements OnInit {

  protected id?: number;
  protected marca?: Marca;
  protected loaderAtivo = false;

  private fb = inject(FormBuilder);
  private marcaService = inject(MarcaService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private loader = inject(NgxUiLoaderService);
  private toastr = inject(ToastrService);

  protected form = this.fb.nonNullable.group({
    descricao: ['', Validators.required]
  });

  public ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id = Number(idParam);
      this.carregar(this.id);
    }
  }

  protected salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const marca: Marca = {
      ...this.marca,
      id: this.id,
      descricao: this.form.controls.descricao.value
    };

    this.loaderAtivo = true;
    this.loader.startLoader('marca-cadastro');
    const requisicao = this.id === undefined
      ? this.marcaService.incluir(marca)
      : this.marcaService.alterar(marca, this.id);

    requisicao
      .pipe(finalize(() => {
        this.loaderAtivo = false;
        this.loader.stopLoader('marca-cadastro');
      }))
      .subscribe({
        next: () => {
          this.toastr.success('Marca salva com sucesso!', 'Sucesso', { timeOut: 3000, progressBar: true, closeButton: true });
          this.router.navigate(['/marca/pesquisar']);
        },
        error: erro => this.tratarErroSalvar(erro)
      });
  }

  protected voltar(): void {
    this.router.navigate(['/marca/pesquisar']);
  }

  private carregar(id: number): void {
    this.loader.startLoader('marca-cadastro');
    this.marcaService.buscar(id)
      .pipe(finalize(() => this.loader.stopLoader('marca-cadastro')))
      .subscribe({
        next: marca => {
          this.marca = marca;
          this.form.patchValue({ descricao: marca.descricao });
        },
        error: () => this.toastr.error('Erro ao carregar Marca.', 'Erro', { timeOut: 5000, progressBar: true, closeButton: true })
      });
  }

  private tratarErroSalvar(erro: { status?: number; error?: { message?: string } }): void {
    if (erro.status === 400 || erro.status === 422) {
      const mensagem = erro.error?.message ?? 'Verifique os dados informados.';
      this.toastr.warning(mensagem, 'Atenção', { timeOut: 5000, progressBar: true, closeButton: true });
      return;
    }
    this.toastr.error('Erro ao salvar Marca. Tente novamente.', 'Erro', { timeOut: 5000, progressBar: true, closeButton: true });
  }

}
