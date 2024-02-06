import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AppConfigService {

  private config: Object = null;

  constructor(private httpClient: HttpClient) { }

  public load() {
    this.httpClient.get<any>(`${environment.apiEndpoint}/config`).subscribe ( (response) => {
      this.config = response;
    });
  }

  public get(key: string) {
    if (this.config != null) {
      return this.config[key];
    }
  }
}
