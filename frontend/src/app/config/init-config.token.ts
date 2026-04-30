import { InjectionToken } from '@angular/core';

export interface InitConfig {
  urlBackend: string;
}

export const INIT_CONFIG = new InjectionToken<InitConfig>('window.initConfig');
