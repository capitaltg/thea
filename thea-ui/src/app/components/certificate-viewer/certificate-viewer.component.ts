import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { ActivatedRoute } from '@angular/router';

import { CertificateService } from '../../../app/services';

@Component({
  selector: 'app-certificate-viewer',
  templateUrl: './certificate-viewer.component.html'
})
export class CertificateViewerComponent implements OnInit {

  certificate: any;

  constructor(
    private certificateService: CertificateService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit() {
    const certificateId = this.route.snapshot.paramMap.get('certificateId');
    if (certificateId) {
      this.certificateService.getCertificate(certificateId).subscribe ((certificate: any) => {
        this.certificate = certificate;
      });
    }
  }

  saveCertificate(sha256) {
    const url = `${environment.apiEndpoint}/certificates/${sha256}/raw`;
    window.open(url);
  }

  saveCertificateChain(sha256) {
    const url = `${environment.apiEndpoint}/certificates/${sha256}/chain`;
    window.open(url);
  }

}
