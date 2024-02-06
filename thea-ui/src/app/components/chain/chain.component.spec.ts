import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { ChainComponent } from './chain.component';
import { CertificateChainComponent } from '../certificate-chain/certificate-chain.component';
import { CertificateComponent } from '../certificate/certificate.component';

describe('ChainComponent', () => {
  let component: ChainComponent;
  let fixture: ComponentFixture<ChainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgbModule,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      declarations: [
        ChainComponent,
        CertificateChainComponent,
        CertificateComponent
       ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
