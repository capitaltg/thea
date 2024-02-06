import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(
    private http: HttpClient,
  ) { }

  searchCertificates(filters: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/certificates?filters=${filters}`);
  }

}
