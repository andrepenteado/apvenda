/*
 * Autor: André Penteado
 * Criado em: 27/05/2026 16:17:35 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { Categoria } from '../../../domain/entities/categoria';
import { CategoriaService } from '../../../services/categoria.service';

@Component({
  selector: 'venda-categoria-cadastro',
  imports: [
    CommonModule,
    NgSelectModule,
    NgxUiLoaderModule,
    ReactiveFormsModule,
    RouterLink
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './cadastro.componente.html'
})
export class CadastroComponente implements OnInit {

  categoria?: Categoria;
  categoriasPai: Categoria[] = [];
  categoriasPaiDisponiveis: Categoria[] = [];
  carregando = false;

  readonly form = inject(FormBuilder).nonNullable.group({
    id: [{ value: undefined as number | undefined, disabled: true }],
    nome: ['', [Validators.required]],
    ativo: [true, [Validators.required]],
    categoriaPai: [null as number | null]
  });

  private readonly loaderId = 'categoria-cadastro';
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly router: Router = inject(Router);
  private readonly service: CategoriaService = inject(CategoriaService);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  get modoEdicao(): boolean {
    return this.categoria?.id != null;
  }

  ngOnInit(): void {
    this.listarCategoriasPai();
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isNaN(id) && id > 0) {
      this.carregar(id);
    }
  }

  carregar(id: number): void {
    this.iniciarLoader();
    this.service.buscar(id).subscribe({
      next: categoria => {
        this.categoria = categoria;
        this.atualizarCategoriasPaiDisponiveis();
        this.form.patchValue({
          id: categoria.id,
          nome: categoria.nome,
          ativo: categoria.ativo,
          categoriaPai: categoria.categoriaPai?.id ?? null
        });
        this.pararLoader();
      },
      error: () => this.pararLoader()
    });
  }

  gravar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const categoria = this.montarCategoria();
    this.iniciarLoader();

    const requisicao = this.modoEdicao && categoria.id != null
      ? this.service.alterar(categoria, categoria.id)
      : this.service.incluir(categoria);

    requisicao.subscribe({
      next: () => {
        this.mensagemService.showMessage('Categoria gravada com sucesso.', 'Categoria', DecoracaoMensagem.SUCESSO);
        this.router.navigate(['/categorias/pesquisar']);
      },
      error: () => this.pararLoader()
    });
  }

  voltar(): void {
    this.router.navigate(['/categorias/pesquisar']);
  }

  campoInvalido(campo: 'nome' | 'ativo'): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && (controle.dirty || controle.touched);
  }

  private listarCategoriasPai(): void {
    this.service.listar().subscribe({
      next: categorias => {
        this.categoriasPai = categorias;
        this.atualizarCategoriasPaiDisponiveis();
      }
    });
  }

  private atualizarCategoriasPaiDisponiveis(): void {
    this.categoriasPaiDisponiveis = this.categoriasPai.filter(categoria => categoria.id !== this.categoria?.id);
  }

  private montarCategoria(): Categoria {
    return {
      id: this.categoria?.id,
      nome: this.form.controls.nome.value,
      ativo: this.form.controls.ativo.value,
      categoriaPai: this.montarCategoriaPai(),
      criadoPor: this.categoria?.criadoPor,
      criadoEm: this.categoria?.criadoEm,
      alteradoPor: this.categoria?.alteradoPor,
      alteradoEm: this.categoria?.alteradoEm
    };
  }

  private montarCategoriaPai(): Categoria | null {
    const id = this.form.controls.categoriaPai.value;

    if (id == null) {
      return null;
    }

    return { id, nome: '', ativo: true };
  }

  private iniciarLoader(): void {
    this.carregando = true;
    this.uiLoaderService.startLoader(this.loaderId);
  }

  private pararLoader(): void {
    this.carregando = false;
    this.uiLoaderService.stopLoader(this.loaderId);
  }

}
