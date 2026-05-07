// André Penteado, 2026-05-06T00:00:00-03:00 - Rotas de Marca criadas com ajuda da IA.
import { Routes } from '@angular/router';

export const MARCA_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'pesquisar',
    pathMatch: 'full'
  },
  {
    path: 'pesquisar',
    loadComponent: () => import('./pesquisar/pesquisar.componente').then(m => m.PesquisarComponente)
  },
  {
    path: 'cadastro',
    loadComponent: () => import('./cadastro/cadastro.componente').then(m => m.CadastroComponente)
  },
  {
    path: 'cadastro/:id',
    loadComponent: () => import('./cadastro/cadastro.componente').then(m => m.CadastroComponente)
  }
];
