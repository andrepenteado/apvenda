/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { Cliente } from '../../../domain/entities/cliente';
import { ClienteService } from '../../../services/cliente.service';

type CampoObrigatorio = 'nome' | 'cpf';

@Component({
  selector: 'venda-cliente-cadastro',
  imports: [
    CommonModule,
    NgxMaskDirective,
    NgxUiLoaderModule,
    ReactiveFormsModule,
    RouterLink
  ],
  providers: [provideNgxMask()],
  templateUrl: './cadastro.componente.html'
})
export class CadastroComponente implements OnInit {

  cliente?: Cliente;
  carregando = false;

  readonly form = inject(FormBuilder).nonNullable.group({
    id: [{ value: undefined as number | undefined, disabled: true }],
    nome: ['', [Validators.required]],
    cpf: ['', [Validators.required]],
    telefone: [null as string | null],
    whatsapp: [false]
  });

  private readonly loaderId = 'cliente-cadastro';
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly router: Router = inject(Router);
  private readonly service: ClienteService = inject(ClienteService);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  get modoEdicao(): boolean {
    return this.cliente?.id != null;
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isNaN(id) && id > 0) {
      this.carregar(id);
    }
  }

  carregar(id: number): void {
    this.iniciarLoader();
    this.service.buscar(id).subscribe({
      next: cliente => {
        this.cliente = cliente;
        this.form.patchValue({
          id: cliente.id,
          nome: cliente.nome,
          cpf: String(cliente.cpf),
          telefone: cliente.telefone ?? null,
          whatsapp: cliente.whatsapp ?? false
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

    const cliente = this.montarCliente();
    this.iniciarLoader();

    const requisicao = this.modoEdicao && cliente.id != null
      ? this.service.alterar(cliente, cliente.id)
      : this.service.incluir(cliente);

    requisicao.subscribe({
      next: () => {
        this.mensagemService.showMessage('Cliente gravado com sucesso.', 'Cliente', DecoracaoMensagem.SUCESSO);
        this.router.navigate(['/clientes/pesquisar']);
      },
      error: () => this.pararLoader()
    });
  }

  voltar(): void {
    this.router.navigate(['/clientes/pesquisar']);
  }

  campoInvalido(campo: CampoObrigatorio): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && (controle.dirty || controle.touched);
  }

  private montarCliente(): Cliente {
    return {
      id: this.cliente?.id,
      nome: this.form.controls.nome.value,
      cpf: Number(this.form.controls.cpf.value),
      telefone: this.form.controls.telefone.value,
      whatsapp: this.form.controls.whatsapp.value,
      criadoPor: this.cliente?.criadoPor,
      criadoEm: this.cliente?.criadoEm,
      alteradoPor: this.cliente?.alteradoPor,
      alteradoEm: this.cliente?.alteradoEm
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
