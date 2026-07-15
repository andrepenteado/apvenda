/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 21:07:14 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { DecoracaoMensagem, ExibirMensagemService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { Cliente } from '../../../domain/entities/cliente';
import { TipoPessoa, TipoPessoaLabels } from '../../../domain/enums/tipo-pessoa';
import { ClienteService } from '../../../services/cliente.service';

type CampoObrigatorio = 'nome' | 'tipoPessoa' | 'cpfCnpj';

const MASCARA_CPF = '000.000.000-00';
const MASCARA_CNPJ = '00.000.000/0000-00';

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
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './cadastro.componente.html'
})
export class CadastroComponente implements OnInit {

  cliente?: Cliente;
  carregando = false;
  mascaraCpfCnpj = MASCARA_CPF;
  readonly tiposPessoa = Object.entries(TipoPessoaLabels).map(([valor, label]) => ({ valor: valor as TipoPessoa, label }));

  readonly form = inject(FormBuilder).nonNullable.group({
    id: [{ value: undefined as number | undefined, disabled: true }],
    nome: ['', [Validators.required]],
    tipoPessoa: [null as TipoPessoa | null, [Validators.required]],
    cpfCnpj: ['', [Validators.required]],
    telefone: [null as string | null],
    whatsapp: [false],
    observacao: [null as string | null]
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
    this.form.controls.tipoPessoa.valueChanges.subscribe(tipoPessoa => {
      this.mascaraCpfCnpj = tipoPessoa === TipoPessoa.JURIDICA ? MASCARA_CNPJ : MASCARA_CPF;
    });

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isNaN(id) && id > 0) {
      this.carregar(id);
    }
  }

  carregar(id: number): void {
    console.info(`Buscar cliente de ID #${id}`);
    this.iniciarLoader();
    this.service.buscar(id).subscribe({
      next: cliente => {
        this.cliente = cliente;
        this.mascaraCpfCnpj = cliente.tipoPessoa === TipoPessoa.JURIDICA ? MASCARA_CNPJ : MASCARA_CPF;
        this.form.patchValue({
          id: cliente.id,
          nome: cliente.nome,
          tipoPessoa: cliente.tipoPessoa,
          cpfCnpj: String(cliente.cpfCnpj),
          telefone: cliente.telefone ?? null,
          whatsapp: cliente.whatsapp ?? false,
          observacao: cliente.observacao ?? null
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
    console.info(`${this.modoEdicao ? 'Alterar dados do' : 'Incluir novo'} cliente ${JSON.stringify(cliente)}`);
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
      tipoPessoa: this.form.controls.tipoPessoa.value as TipoPessoa,
      cpfCnpj: Number(this.form.controls.cpfCnpj.value),
      telefone: this.form.controls.telefone.value,
      whatsapp: this.form.controls.whatsapp.value,
      observacao: this.form.controls.observacao.value,
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
