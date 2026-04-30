import { Routes } from '@angular/router';
import {
  AuthorizedComponent,
  LayoutPadraoComponent,
  LoginComponent,
  LogoutComponent, SemLayoutComponent
} from '@andre.penteado/ngx-apcore';
import { DECORATED_ROUTES, NO_DECORATED_ROUTES } from './config/routes';

export const routes: Routes = [

  { path: '', redirectTo: '/pagina-inicial', pathMatch: 'full' },

  { path: 'login', component: LoginComponent },

  { path: 'logout', component: LogoutComponent },

  { path: 'authorized', component: AuthorizedComponent },

  {
    path: "",
    component: LayoutPadraoComponent,
    children: DECORATED_ROUTES
  },

  {
    path: "",
    component: SemLayoutComponent,
    children: NO_DECORATED_ROUTES
  }

];
