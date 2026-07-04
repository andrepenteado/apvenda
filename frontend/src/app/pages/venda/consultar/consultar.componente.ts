/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { FormaPagamento, FormaPagamentoLabels } from '../../../domain/enums/forma-pagamento';
import { VendaResponse, VendaService } from '../../../services/venda.service';
import { ImprimirVendaComponente } from '../imprimir/imprimir.componente';

@Component({
  selector: 'venda-consultar',
  imports: [
    CommonModule,
    NgxUiLoaderModule,
    RouterLink,
    ImprimirVendaComponente
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './consultar.componente.html'
})
export class ConsultarComponente implements OnInit {

  venda?: VendaResponse;
  impressaoAberta = false;

  private readonly loaderId = 'venda-consultar';
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly service: VendaService = inject(VendaService);
  private readonly uiLoaderService: NgxUiLoaderService = inject(NgxUiLoaderService);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.buscar(id).subscribe({
      next: venda => {
        this.venda = venda;
        this.uiLoaderService.stopLoader(this.loaderId);
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });
  }

  formaPagamentoLabel(formaPagamento: FormaPagamento): string {
    return FormaPagamentoLabels[formaPagamento];
  }

  abrirImpressao(): void {
    this.impressaoAberta = true;
  }

  fecharImpressao(): void {
    this.impressaoAberta = false;
  }

}
