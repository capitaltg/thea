import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CertificateService } from '../../../app/services';

@Component({
  selector: 'app-chain',
  templateUrl: './history.component.html'
})
export class HistoryComponent implements OnInit {

    historicalChains: any[];
    hostname: string;

    constructor(private certificateService: CertificateService,
        private route: ActivatedRoute,
        private router: Router) { }

    ngOnInit() {
        const hostname = this.route.snapshot.paramMap.get('hostname');
        this.hostname = hostname;
        this.showHistory(hostname);
    }

    showHistory (hostname): void {

        this.certificateService.getCertificateChainsByHostname(hostname).subscribe((response: any) => {
          this.historicalChains = response;
        }, () => {
          console.log(`Failed to find chain history for ${hostname}. Please try again.`);
        } );
      }
}
