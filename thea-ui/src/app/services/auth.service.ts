import { DOCUMENT } from '@angular/common';
import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    @Inject(DOCUMENT) private document: any,
    private http: HttpClient,
    private router: Router
  ) {
    this.loadProfile();
  }

  userProfile: any;

  public login(): void {
    console.log('logginging in');
    document.location.href = '/login';
  }

  public logout(): void {
    console.log('logginging out');
    this.http.get<any>('/api/logout').subscribe ( () => {
      this.userProfile = undefined;
      this.router.navigate(['/home']);
    });
  }

  public isAuthenticated(): boolean {
    return this.userProfile;
  }

  public getProfile(): void {
    return this.userProfile;
  }

  private loadProfile(): void {
    this.http.get<any>('/api/user').subscribe ( user => {
      console.log('got', user);
      this.userProfile = user;
    });
  }

}
