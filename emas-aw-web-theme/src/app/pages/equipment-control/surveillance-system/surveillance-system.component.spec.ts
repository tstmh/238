import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveillanceSystemComponent } from './surveillance-system.component';

describe('SurveillanceSystemComponent', () => {
  let component: SurveillanceSystemComponent;
  let fixture: ComponentFixture<SurveillanceSystemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SurveillanceSystemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveillanceSystemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
