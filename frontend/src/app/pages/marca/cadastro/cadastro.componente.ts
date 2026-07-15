/*
 * Autor: André Penteado
 * Criado em: 26/05/2026 17:21:01 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { Marca } from '../../../domain/entities/marca';
import { MarcaService } from '../../../services/marca.service';

@Component({
  selector: 'venda-marca-cadastro',
  imports: [
    CommonModule,
    NgxUiLoaderModule,
    ReactiveFormsModule,
    RouterLink
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './cadastro.componente.html'
})
export class CadastroComponente implements OnInit {

  marca?: Marca;
  carregando = false;

  readonly form = inject(FormBuilder).nonNullable.group({
    id: [{ value: undefined as number | undefined, disabled: true }],
    nome: ['', [Validators.required]]
  });

  private readonly loaderId = 'marca-cadastro';
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly router: Router = inject(Router);
  private readonly service: MarcaService = inject(MarcaService);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  get modoEdicao(): boolean {
    return this.marca?.id != null;
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isNaN(id) && id > 0) {
      this.carregar(id);
    }
  }

  carregar(id: number): void {
    console.info(`Buscar marca de ID #${id}`);
    this.iniciarLoader();
    this.service.buscar(id).subscribe({
      next: marca => {
        this.marca = marca;
        this.form.patchValue({
          id: marca.id,
          nome: marca.nome
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

    const marca = this.montarMarca();
    console.info(`${this.modoEdicao ? 'Alterar dados da' : 'Incluir nova'} marca ${JSON.stringify(marca)}`);
    this.iniciarLoader();

    const requisicao = this.modoEdicao && marca.id != null
      ? this.service.alterar(marca, marca.id)
      : this.service.incluir(marca);

    requisicao.subscribe({
      next: () => {
        this.mensagemService.showMessage('Marca gravada com sucesso.', 'Marca', DecoracaoMensagem.SUCESSO);
        this.router.navigate(['/marcas/pesquisar']);
      },
      error: () => this.pararLoader()
    });
  }

  voltar(): void {
    this.router.navigate(['/marcas/pesquisar']);
  }

  campoInvalido(campo: 'nome'): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && (controle.dirty || controle.touched);
  }

  private montarMarca(): Marca {
    return {
      id: this.marca?.id,
      nome: this.form.controls.nome.value,
      criadoPor: this.marca?.criadoPor,
      criadoEm: this.marca?.criadoEm,
      alteradoPor: this.marca?.alteradoPor,
      alteradoEm: this.marca?.alteradoEm
    };
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
