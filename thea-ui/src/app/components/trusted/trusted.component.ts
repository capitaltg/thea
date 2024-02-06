import { Component, OnInit } from '@angular/core';

import { CertificateService } from '../../../app/services';

@Component({
  selector: 'app-trusted',
  templateUrl: './trusted.component.html'
})
export class TrustedComponent implements OnInit {

  trusted: any[];

  constructor(
    private certificateService: CertificateService,
  ) { }

  ngOnInit() {
    this.certificateService.getTrustedCertificates().subscribe ( results => this.trusted = results );
  }

  isExpired(date): boolean {
    return Date.parse(date) < Date.now();
  }
}
