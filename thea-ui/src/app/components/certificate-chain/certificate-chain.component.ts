import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { CertificateService } from '../../../app/services';

@Component({
  selector: 'app-certificate-chain',
  templateUrl: './certificate-chain.component.html'
})
export class CertificateChainComponent implements OnInit {

  @Input() chain: any;

  constructor(
    private certificateService: CertificateService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit() {
    const certificateChainId = this.route.snapshot.paramMap.get('certificateChainId');
    if (certificateChainId) {
      this.certificateService.getCertificateChain(certificateChainId).subscribe ( response => this.chain = response );
    }
  }

  getWarnings(certificates: any[]) {
    return certificates.map(certificate => certificate.warnings).reduce( (list, value) => list.concat(value), [] );
  }

}
