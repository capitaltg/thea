import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {
  constructor(
    private http: HttpClient
  ) { }

  getCertificate(certificateId: string) {
    return this.http.get<any>(`${environment.apiEndpoint}/certificates/${certificateId}`);
  }

  getTrustedCertificates() {
    return this.http.get<any>(`${environment.apiEndpoint}/certificates/trusted`);
  }

  getSimilarCertificates(sha256: string) {
    return this.http.get<any>(`${environment.apiEndpoint}/certificates/${sha256}/similar`);
  }

  getChainsContaining(sha256: string) {
    return this.http.get<any>(`${environment.apiEndpoint}/chains/includes/${sha256}`);
  }

  getNewCertificateChain(hostname: string, hideResult = false) {
    const params = new HttpParams()
      .append('hostname', hostname)
      .append('hideResult', hideResult.toString());
    return this.http.post<any>(`${environment.apiEndpoint}/chains`, { }, { params });
  }

  getCertificateChain(certificateChainId: string) {
    return this.http.get<any>(`${environment.apiEndpoint}/chains/${certificateChainId}`);
  }

  getRecentChains(page: number) {
    return this.http.get<any>(`${environment.apiEndpoint}/chains/recent?page=${page}`);
  }

  getCertificateChainsByHostname(hostname: string) {
    return this.http.get<any>(`${environment.apiEndpoint}/chains/historical/${hostname}`);
  }

}
