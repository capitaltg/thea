import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {
  AboutComponent,
  CallbackComponent,
  ChainComponent,
  CertificateViewerComponent,
  HomeComponent,
  ProfileComponent,
  ReportsComponent,
  SearchComponent,
  TrustedComponent,
} from './components';

const routes: Routes = [
  { path: 'about', component: AboutComponent },
  { path: 'callback', component: CallbackComponent },
  { path: 'chain/:certificateChainId', component: ChainComponent },
  { path: 'certificate/:certificateId', component: CertificateViewerComponent },
  { path: 'home', component: HomeComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'reports', component: ReportsComponent },
  { path: 'search', component: SearchComponent },
  { path: 'trusted', component: TrustedComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', redirectTo: '/home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
