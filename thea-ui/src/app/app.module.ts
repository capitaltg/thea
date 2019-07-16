import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { CallbackComponent } from './components/callback/callback.component';

import {
  AboutComponent,
  AppNavbarComponent,
  CertificateComponent,
  ChainComponent,
  HomeComponent,
  ProfileComponent,
  RecentComponent,
  ReportsComponent,
  SearchComponent,
  TrustedComponent,
} from './components';

import { CertificateViewerComponent } from './components/certificate-viewer/certificate-viewer.component';
import { CertificateChainComponent } from './components/certificate-chain/certificate-chain.component';

@NgModule({
  declarations: [
    AboutComponent,
    AppComponent,
    AppNavbarComponent,
    CallbackComponent,
    CertificateComponent,
    CertificateViewerComponent,
    ProfileComponent,
    RecentComponent,
    ReportsComponent,
    SearchComponent,
    CertificateChainComponent,
    ChainComponent,
    TrustedComponent,
    HomeComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    NgbModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
