/*
 * Autor: André Penteado
 * Criado em: 02/07/2026 23:20:00 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild, inject, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgxUiLoaderModule, NgxUiLoaderService } from 'ngx-ui-loader';
import { Chart, registerables, TooltipItem } from 'chart.js';
import { FormaPagamentoLabels } from '../../domain/enums/forma-pagamento';
import { DashboardResponse, VendaService } from '../../services/venda.service';

Chart.register(...registerables);

/**
 * Paleta categórica validada (dataviz), com passos próprios para cada tema.
 * A ordem dos slots é fixa: ela garante a separação para daltonismo.
 */
const SERIES = {
  light: ['#2a78d6', '#1baf7a', '#eda100', '#008300'],
  dark: ['#3987e5', '#199e70', '#c98500', '#008300']
};

const MOEDA = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

@Component({
  selector: 'venda-dashboard',
  imports: [
    CommonModule,
    NgxUiLoaderModule,
    RouterLink
  ],
  templateUrl: './dashboard.componente.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './dashboard.componente.css'
})
export class DashboardComponente implements OnInit, AfterViewInit, OnDestroy {

  dados: DashboardResponse | null = null;

  @ViewChild('graficoVendasDia') graficoVendasDia?: ElementRef<HTMLCanvasElement>;
  @ViewChild('graficoTopProdutos') graficoTopProdutos?: ElementRef<HTMLCanvasElement>;
  @ViewChild('graficoFormasPagamento') graficoFormasPagamento?: ElementRef<HTMLCanvasElement>;

  private readonly loaderId = 'dashboard';
  private readonly service = inject(VendaService);
  private readonly uiLoaderService = inject(NgxUiLoaderService);
  private graficos: Chart[] = [];
  private observadorTema?: MutationObserver;
  private visaoPronta = false;

  ngOnInit(): void {
    this.uiLoaderService.startLoader(this.loaderId);
    this.service.dashboard().subscribe({
      next: dados => {
        this.dados = dados;
        this.uiLoaderService.stopLoader(this.loaderId);
        if (this.visaoPronta) {
          setTimeout(() => this.montarGraficos(), 0);
        }
      },
      error: () => this.uiLoaderService.stopLoader(this.loaderId)
    });

    // Os gráficos usam cores resolvidas em JS: é preciso remontar ao trocar o tema.
    this.observadorTema = new MutationObserver(() => this.montarGraficos());
    this.observadorTema.observe(document.body, { attributes: true, attributeFilter: ['data-bs-theme'] });
  }

  ngAfterViewInit(): void {
    this.visaoPronta = true;
    if (this.dados) {
      setTimeout(() => this.montarGraficos(), 0);
    }
  }

  ngOnDestroy(): void {
    this.observadorTema?.disconnect();
    this.destruirGraficos();
  }

  get temFormasPagamento(): boolean {
    return (this.dados?.formasPagamento?.length ?? 0) > 0;
  }

  labelFormaPagamento(indice: number): string {
    const forma = this.dados?.formasPagamento[indice]?.formaPagamento;
    return forma ? FormaPagamentoLabels[forma] : '';
  }

  corFormaPagamento(indice: number): string {
    return SERIES[this.tema()][indice % SERIES.light.length];
  }

  formatarMoeda(valor: number): string {
    return MOEDA.format(valor ?? 0);
  }

  private tema(): 'light' | 'dark' {
    return document.body.getAttribute('data-bs-theme') === 'dark' ? 'dark' : 'light';
  }

  private token(nome: string): string {
    return getComputedStyle(document.body).getPropertyValue(nome).trim();
  }

  private montarGraficos(): void {
    if (!this.dados || !this.visaoPronta) {
      return;
    }
    this.destruirGraficos();

    const series = SERIES[this.tema()];
    const grade = this.token('--bs-border-color');
    const tinta = this.token('--bs-secondary-color');
    const superficie = this.token('--bs-body-bg');

    Chart.defaults.color = tinta;
    Chart.defaults.font.family = getComputedStyle(document.body).fontFamily;

    this.montarVendasPorDia(series[0], grade, tinta);
    this.montarTopProdutos(series[0], grade, tinta);
    this.montarFormasPagamento(series, superficie);
  }

  private montarVendasPorDia(cor: string, grade: string, tinta: string): void {
    const canvas = this.graficoVendasDia?.nativeElement;
    if (!canvas || !this.dados) {
      return;
    }
    const dias = this.dados.vendasPorDia;
    const rotulos = dias.map(dia => {
      const [, mes, diaMes] = dia.data.split('-');
      return `${diaMes}/${mes}`;
    });

    this.graficos.push(new Chart(canvas, {
      type: 'line',
      data: {
        labels: rotulos,
        datasets: [{
          data: dias.map(dia => dia.total),
          borderColor: cor,
          backgroundColor: this.comAlfa(cor, 0.1),
          fill: true,
          borderWidth: 2,
          pointRadius: 0,
          pointHoverRadius: 5,
          pointHoverBackgroundColor: cor,
          tension: 0.3
        }]
      },
      options: {
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (item: TooltipItem<'line'>) => {
                const dia = this.dados!.vendasPorDia[item.dataIndex];
                return ` ${this.formatarMoeda(dia.total)} · ${dia.quantidade} venda${dia.quantidade === 1 ? '' : 's'}`;
              }
            }
          }
        },
        scales: {
          x: { grid: { display: false }, ticks: { color: tinta, maxTicksLimit: 10 } },
          y: {
            beginAtZero: true,
            grid: { color: grade },
            border: { display: false },
            ticks: { color: tinta, callback: valor => this.formatarMoeda(Number(valor)) }
          }
        }
      }
    }));
  }

  private montarTopProdutos(cor: string, grade: string, tinta: string): void {
    const canvas = this.graficoTopProdutos?.nativeElement;
    if (!canvas || !this.dados) {
      return;
    }
    const produtos = this.dados.topProdutos;

    this.graficos.push(new Chart(canvas, {
      type: 'bar',
      data: {
        labels: produtos.map(produto => this.abreviar(produto.nome, 28)),
        datasets: [{
          data: produtos.map(produto => produto.total),
          backgroundColor: cor,
          maxBarThickness: 24,
          borderRadius: { topRight: 4, bottomRight: 4 },
          borderSkipped: 'start'
        }]
      },
      options: {
        indexAxis: 'y',
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (item: TooltipItem<'bar'>) => {
                const produto = this.dados!.topProdutos[item.dataIndex];
                return ` ${this.formatarMoeda(produto.total)} · qtd ${produto.quantidade}`;
              }
            }
          }
        },
        scales: {
          x: {
            beginAtZero: true,
            grid: { color: grade },
            border: { display: false },
            ticks: { color: tinta, maxTicksLimit: 6, callback: valor => this.formatarMoeda(Number(valor)) }
          },
          y: { grid: { display: false }, ticks: { color: tinta } }
        }
      }
    }));
  }

  private montarFormasPagamento(series: string[], superficie: string): void {
    const canvas = this.graficoFormasPagamento?.nativeElement;
    if (!canvas || !this.dados || !this.temFormasPagamento) {
      return;
    }
    const formas = this.dados.formasPagamento;

    this.graficos.push(new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: formas.map(forma => FormaPagamentoLabels[forma.formaPagamento]),
        datasets: [{
          data: formas.map(forma => forma.total),
          backgroundColor: formas.map((_, indice) => series[indice % series.length]),
          borderColor: superficie,
          borderWidth: 2
        }]
      },
      options: {
        maintainAspectRatio: false,
        cutout: '65%',
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (item: TooltipItem<'doughnut'>) => ` ${this.formatarMoeda(formas[item.dataIndex].total)}`
            }
          }
        }
      }
    }));
  }

  private comAlfa(hex: string, alfa: number): string {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${alfa})`;
  }

  private abreviar(texto: string, tamanho: number): string {
    return texto.length > tamanho ? `${texto.slice(0, tamanho - 1)}…` : texto;
  }

  private destruirGraficos(): void {
    this.graficos.forEach(grafico => grafico.destroy());
    this.graficos = [];
  }

}
