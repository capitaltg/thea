import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificateChainComponent } from './certificate-chain.component';

describe('CertificateChainComponent', () => {
  let component: CertificateChainComponent;
  let fixture: ComponentFixture<CertificateChainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CertificateChainComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CertificateChainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
