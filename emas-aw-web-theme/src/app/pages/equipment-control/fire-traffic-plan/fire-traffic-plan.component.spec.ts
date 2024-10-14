import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FireTrafficPlanComponent } from './fire-traffic-plan.component';

describe('FireTrafficPlanComponent', () => {
  let component: FireTrafficPlanComponent;
  let fixture: ComponentFixture<FireTrafficPlanComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FireTrafficPlanComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FireTrafficPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
