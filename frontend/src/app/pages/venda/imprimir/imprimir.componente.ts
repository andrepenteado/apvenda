/*
 * Autor: André Penteado
 * Criado em: 04/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, ChangeDetectionStrategy } from '@angular/core';
import { Unidade, UnidadeLabels } from '../../../domain/enums/unidade';

/** Item mínimo exigido para a impressão: compatível tanto com o carrinho do PDV quanto com o item já gravado (ItemConsolidado). */
export interface ItemImpressao {
  nome: string;
  unidade?: Unidade | null;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
}

@Component({
  selector: 'venda-imprimir',
  imports: [CommonModule],
  templateUrl: './imprimir.componente.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './imprimir.componente.css'
})
export class ImprimirVendaComponente {

  @Input() aberto = false;
  @Input() vendaId: number | null = null;
  @Input() nomeCliente = 'CONSUMIDOR';
  @Input() telefoneCliente: string | null = null;
  @Input() itens: ItemImpressao[] = [];
  @Input() total = 0;

  @Output() fechar = new EventEmitter<void>();

  get cabecalho(): string {
    return this.vendaId != null ? `VENDA #${this.vendaId}` : 'ORÇAMENTO';
  }

  unidadeLabel(unidade?: Unidade | null): string {
    return unidade ? UnidadeLabels[unidade] : '';
  }

  fecharClick(): void {
    this.fechar.emit();
  }

  imprimir(): void {
    window.print();
  }

}
