import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SearchService } from '../../services/search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit {

  filters: {[key: string]: string} = {};
  certificates: any[];

  constructor(
    private searchService: SearchService,
    private route: ActivatedRoute,
    private router: Router,
    ) { }

  ngOnInit() {
    const filters = this.route.snapshot.queryParams.filters;
    if (filters) {
      this.searchService.searchCertificates(filters).subscribe ((certificates: any[]) => {
        this.certificates = certificates;
      });
    }
  }

  search() {
    const string = this.mapToString(this.filters);
    this.searchService.searchCertificates(string).subscribe ((certificates: any[]) => {
      this.router.navigate(['/search'], { queryParams: { filters : string } });
      this.certificates = certificates;
    });
  }

  mapToString(filters: Object): string {
    let string = '';
    Object.entries(filters).forEach ( (entry) => {
      const key = entry[0];
      const value = entry[1];
      if (key && value) {
        string = string.concat(`${key}:${value},`);
      }
    });
    return string;
  }

}
