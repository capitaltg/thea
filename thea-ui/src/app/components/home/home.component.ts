import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CertificateService } from '../../../app/services';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

  chain: any;
  error: string;
  hostname: string;
  hideResult = false;
  searching = false;

  constructor(private certificateService: CertificateService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.hostname = this.route.snapshot.queryParams.hostname;
    this.hideResult = String(this.route.snapshot.queryParams.hideResult) === 'true';
    if (this.hostname) {
      this.searching = true;
      this.getNewCertificateChain(this.hostname);
    }
  }

  private extractHostname(hostname: string) {
    hostname = hostname.trim();
    const regex = new RegExp('^(?:f|ht)tp(?:s)?\://([^/]+)', 'im');
    const regex2 = new RegExp('^((?:f|ht)tp(?:s)?\://)?([^/]+)', 'im');
    if (hostname.match(regex)) {
      return hostname.match(regex)[1].toString();
    } else if (hostname.match(regex2)) {
      return hostname.match(regex2)[2].toString();
    }
    return hostname;
  }

  private getNewCertificateChain(hostname) {
    this.hostname = this.extractHostname(hostname);
    this.searching = true;
    this.certificateService.getNewCertificateChain(this.hostname, this.hideResult).subscribe((response: any) => {
      this.chain = response;
      this.searching = false;
    }, () => {
      this.searching = false;
      this.error = `Failed to inspect certificate for ${this.hostname}. Please try again.`;
    } );
  }

  redirect(): void {
    this.error = null;
    this.hostname = this.extractHostname(this.hostname);
    this.router.navigate(['/home'], { queryParams: { hostname : this.hostname, hideResult : this.hideResult } });
  }

}
