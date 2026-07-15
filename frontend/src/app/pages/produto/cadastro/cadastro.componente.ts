/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { DecoracaoMensagem, ExibirMensagemService, ImagemUploadComponent, Upload, UploadService } from '@andre.penteado/ngx-apcore';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { Categoria } from '../../../domain/entities/categoria';
import { Marca } from '../../../domain/entities/marca';
import { Produto } from '../../../domain/entities/produto';
import { Unidade, UnidadeLabels } from '../../../domain/enums/unidade';
import { CategoriaService } from '../../../services/categoria.service';
import { MarcaService } from '../../../services/marca.service';
import { ProdutoService } from '../../../services/produto.service';

type CampoObrigatorio = 'nome' | 'categoria' | 'marca' | 'unidade' | 'ativo';

@Component({
  selector: 'venda-produto-cadastro',
  imports: [
    CommonModule,
    NgSelectModule,
    NgxUiLoaderModule,
    ReactiveFormsModule,
    RouterLink,
    ImagemUploadComponent
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './cadastro.componente.html'
})
export class CadastroComponente implements OnInit {

  produto?: Produto;
  categorias: Categoria[] = [];
  marcas: Marca[] = [];
  unidades = Object.entries(UnidadeLabels).map(([valor, label]) => ({ valor: valor as Unidade, label }));
  fotoUpload: Upload | null = null;
  fotoUuid: string | null = null;
  carregando = false;

  readonly form = inject(FormBuilder).nonNullable.group({
    id: [{ value: undefined as number | undefined, disabled: true }],
    nome: ['', [Validators.required]],
    codigoBarras: [null as string | null],
    categoria: [null as number | null, [Validators.required]],
    marca: [null as number | null, [Validators.required]],
    unidade: [null as Unidade | null, [Validators.required]],
    precoVenda: [null as number | null],
    custoCompra: [null as number | null],
    estoqueAtual: [null as number | null],
    ativo: [true, [Validators.required]],
    observacao: [null as string | null]
  });

  private readonly loaderId = 'produto-cadastro';
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly router: Router = inject(Router);
  private readonly service: ProdutoService = inject(ProdutoService);
  private readonly categoriaService: CategoriaService = inject(CategoriaService);
  private readonly marcaService: MarcaService = inject(MarcaService);
  private readonly uploadService: UploadService = inject(UploadService);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);
  private readonly mensagemService: ExibirMensagemService = inject(ExibirMensagemService);

  get modoEdicao(): boolean {
    return this.produto?.id != null;
  }

  get fotoSrc(): string | null {
    return this.fotoUpload ? `data:${this.fotoUpload.tipoMime};base64,${this.fotoUpload.base64}` : null;
  }

  ngOnInit(): void {
    this.listarCategorias();
    this.listarMarcas();
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isNaN(id) && id > 0) {
      this.carregar(id);
    }
  }

  carregar(id: number): void {
    console.info(`Buscar produto de ID #${id}`);
    this.iniciarLoader();
    this.service.buscar(id).subscribe({
      next: produto => {
        this.produto = produto;
        this.fotoUuid = produto.foto ?? null;
        this.form.patchValue({
          id: produto.id,
          nome: produto.nome,
          codigoBarras: produto.codigoBarras ?? null,
          categoria: produto.categoria?.id ?? null,
          marca: produto.marca?.id ?? null,
          unidade: produto.unidade,
          precoVenda: produto.precoVenda ?? null,
          custoCompra: produto.custoCompra ?? null,
          estoqueAtual: produto.estoqueAtual ?? null,
          ativo: produto.ativo,
          observacao: produto.observacao ?? null
        });
        this.carregarFoto();
        this.pararLoader();
      },
      error: () => this.pararLoader()
    });
  }

  selecionarFoto(arquivo: File): void {
    const reader = new FileReader();
    reader.onload = () => {
      const base64 = (reader.result as string).split(',')[1] ?? '';
      const upload: Upload = {
        uuid: crypto.randomUUID(),
        nome: arquivo.name,
        descricao: arquivo.name,
        tipoMime: arquivo.type,
        tamanho: arquivo.size,
        base64
      };

      this.iniciarLoader();
      this.uploadService.incluir(upload).subscribe({
        next: salvo => {
          this.fotoUpload = salvo;
          this.fotoUuid = salvo.uuid;
          this.pararLoader();
        },
        error: () => this.pararLoader()
      });
    };
    reader.readAsDataURL(arquivo);
  }

  removerFoto(): void {
    this.fotoUpload = null;
    this.fotoUuid = null;
  }

  gravar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const produto = this.montarProduto();
    console.info(`${this.modoEdicao ? 'Alterar dados do' : 'Incluir novo'} produto ${JSON.stringify(produto)}`);
    this.iniciarLoader();

    const requisicao = this.modoEdicao && produto.id != null
      ? this.service.alterar(produto, produto.id)
      : this.service.incluir(produto);

    requisicao.subscribe({
      next: () => {
        this.mensagemService.showMessage('Produto gravado com sucesso.', 'Produto', DecoracaoMensagem.SUCESSO);
        this.router.navigate(['/produtos/pesquisar']);
      },
      error: () => this.pararLoader()
    });
  }

  voltar(): void {
    this.router.navigate(['/produtos/pesquisar']);
  }

  campoInvalido(campo: CampoObrigatorio): boolean {
    const controle = this.form.controls[campo];
    return controle.invalid && (controle.dirty || controle.touched);
  }

  private carregarFoto(): void {
    if (!this.fotoUuid) {
      this.fotoUpload = null;
      return;
    }

    this.uploadService.buscar(this.fotoUuid).subscribe({
      next: upload => {
        this.fotoUpload = upload;
      }
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

  private montarProduto(): Produto {
    const categoriaId = this.form.controls.categoria.value;
    const marcaId = this.form.controls.marca.value;

    return {
      id: this.produto?.id,
      nome: this.form.controls.nome.value,
      codigoBarras: this.form.controls.codigoBarras.value,
      categoria: { id: categoriaId ?? undefined, nome: '', ativo: true },
      marca: { id: marcaId ?? undefined, nome: '' },
      unidade: this.form.controls.unidade.value as Unidade,
      precoVenda: this.form.controls.precoVenda.value,
      custoCompra: this.form.controls.custoCompra.value,
      estoqueAtual: this.form.controls.estoqueAtual.value,
      ativo: this.form.controls.ativo.value,
      foto: this.fotoUuid,
      observacao: this.form.controls.observacao.value,
      criadoPor: this.produto?.criadoPor,
      criadoEm: this.produto?.criadoEm,
      alteradoPor: this.produto?.alteradoPor,
      alteradoEm: this.produto?.alteradoEm
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
