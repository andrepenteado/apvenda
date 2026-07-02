/*
 * Autor: André Penteado
 * Criado em: 01/07/2026 -03
 * Observação: arquivo criado com ajuda da IA.
 */
import { Routes } from '@angular/router';

export const PRODUTO_ROUTES: Routes = [

  {
    path: '',
    redirectTo: 'pesquisar',
    pathMatch: 'full'
  },

  {
    path: 'pesquisar',
    loadComponent: () => import('./pesquisar/pesquisar.componente').then(c => c.PesquisarComponente)
  },

  {
    path: 'cadastro',
    loadComponent: () => import('./cadastro/cadastro.componente').then(c => c.CadastroComponente)
  },

  {
    path: 'cadastro/:id',
    loadComponent: () => import('./cadastro/cadastro.componente').then(c => c.CadastroComponente)
  }

];
