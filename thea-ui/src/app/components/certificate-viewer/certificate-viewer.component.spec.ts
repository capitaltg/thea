import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { CertificateViewerComponent } from './certificate-viewer.component';
import { CertificateComponent } from '../certificate/certificate.component';

describe('CertificateViewerComponent', () => {
  let component: CertificateViewerComponent;
  let fixture: ComponentFixture<CertificateViewerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgbModule,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      declarations: [
        CertificateViewerComponent,
        CertificateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CertificateViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
