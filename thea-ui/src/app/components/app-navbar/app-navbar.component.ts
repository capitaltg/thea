import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './app-navbar.component.html'
})
export class AppNavbarComponent implements OnInit {

  auth: AuthService;

  constructor(private authService: AuthService) {
    this.auth = authService;
  }

  ngOnInit() {
  }

  login() {
    this.auth.login();
  }

}
