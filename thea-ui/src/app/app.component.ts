import { Component } from '@angular/core';
import { AppConfigService, AuthService } from './services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  constructor(private auth: AuthService,
    appConfigService: AppConfigService) {
    auth.handleAuthentication();
    appConfigService.load();
  }

}
