import { bootstrapApplication } from '@angular/platform-browser';
import { initFaro } from '@andre.penteado/ngx-apcore';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { environment } from './environments/environment';
import packageJson from '../package.json';

// Antes do bootstrap, para capturar erros e métricas do próprio carregamento
initFaro({
  appName: 'venda-frontend',
  appVersion: packageJson.version,
  enabled: environment.production
});

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
