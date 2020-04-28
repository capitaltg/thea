import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CertificateService } from '../../../app/services';

@Component({
  selector: 'app-chain',
  templateUrl: './chain.component.html'
})
export class ChainComponent implements OnInit {

  chain: any;
  historicalChains: any[];
  error: string;
  hostname: string;

  constructor(private certificateService: CertificateService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    const certificateChainId = this.route.snapshot.paramMap.get('certificateChainId');
    if (certificateChainId) {
      this.certificateService.getCertificateChain(certificateChainId).subscribe (
        response => {
          this.chain = response;
        });
    }
  }

  reinspect(): void {
    this.getNewCertificateChain(this.chain.hostname);
    this.error = null;
  }

  showHistory(): void {
    this.certificateService.getCertificateChainsByHostname(this.chain.hostname).subscribe((response: any) => {
      this.historicalChains = response;
    }, () => {
      this.error = `Failed to find chain history for ${this.chain.hostname}. Please try again.`;
    } );
  }

  private getNewCertificateChain(hostname: string) {
    this.certificateService.getNewCertificateChain(hostname).subscribe((response: any) => {
      this.chain = response;
      this.router.navigate([`/chain/${this.chain.certificateChainId}`]);
    }, () => {
      this.error = `Failed to inspect certificate for ${hostname}. Please try again.`;
    } );
  }

}
