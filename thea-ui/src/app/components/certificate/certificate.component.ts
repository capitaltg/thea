import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Router } from '@angular/router';

import { CertificateService } from '../../../app/services';

@Component({
  selector: 'app-certificate',
  templateUrl: './certificate.component.html'
})
export class CertificateComponent implements OnInit {

  @Input() certificate: any;
  @Input() index: number;

  public collapsed = true;
  similar: any[];
  chains: any[];

  constructor(
    private certificateService: CertificateService,
    private router: Router,
  ) { }

  ngOnInit() {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    if (this.index === undefined) {
      this.getSimilar(this.certificate.sha256);
      this.getChainsContaining(this.certificate.sha256);
    }
  }

  isExpired(date): boolean {
    return Date.parse(date) < Date.now();
  }

  getSimilar(sha256) {
    this.certificateService.getSimilarCertificates(sha256).subscribe ( response => this.similar = response );
  }

  getChainsContaining(sha256) {
    this.certificateService.getChainsContaining(sha256).subscribe ( response => this.chains = response );
  }

}
