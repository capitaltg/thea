import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { AppConfigService } from '../../services';

@Component({
  selector: 'app-navbar',
  templateUrl: './app-navbar.component.html'
})
export class AppNavbarComponent implements OnInit {

  appConfigService: AppConfigService;
  auth: AuthService;

  constructor(private authService: AuthService,
    appConfigService: AppConfigService) {
    this.auth = authService;
    this.appConfigService = appConfigService;
  }

  ngOnInit() {
  }

  login() {
    this.auth.login();
  }

}
