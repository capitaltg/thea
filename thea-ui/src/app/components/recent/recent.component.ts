import { Component, OnInit } from '@angular/core';
import { CertificateService } from '../../services/certificate.service';

@Component({
  selector: 'app-recent',
  templateUrl: './recent.component.html'
})
export class RecentComponent implements OnInit {

  recentChains: any;

  constructor(
    private certificateService: CertificateService,
  ) { }

  ngOnInit() {
    this.getRecentChains(0);
  }

  getRecentChains(page: number) {
    this.certificateService.getRecentChains(page).subscribe( (response) => this.recentChains = response);
  }

}
