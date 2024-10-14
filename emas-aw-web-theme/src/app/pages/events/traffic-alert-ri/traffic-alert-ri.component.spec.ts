import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficAlertRiComponent } from './traffic-alert-ri.component';

describe('TrafficAlertRiComponent', () => {
  let component: TrafficAlertRiComponent;
  let fixture: ComponentFixture<TrafficAlertRiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TrafficAlertRiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrafficAlertRiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
