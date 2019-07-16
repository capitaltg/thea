import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TrustedComponent } from './trusted.component';

describe('TrustedComponent', () => {
  let component: TrustedComponent;
  let fixture: ComponentFixture<TrustedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TrustedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrustedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
