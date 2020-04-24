import { Component, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-certificate-chain',
  templateUrl: './certificate-chain.component.html'
})
export class CertificateChainComponent implements OnInit {

  @Input() chain: any;

  constructor() { }

  ngOnInit() { }

  getWarnings(certificates: any[]) {
    return certificates.map(certificate => certificate.warnings).reduce( (list, value) => list.concat(value), [] );
  }

}
