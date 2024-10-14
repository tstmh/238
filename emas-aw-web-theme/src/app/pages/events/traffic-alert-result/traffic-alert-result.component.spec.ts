import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficAlertResultComponent } from './traffic-alert-result.component';

describe('TrafficAlertResultComponent', () => {
  let component: TrafficAlertResultComponent;
  let fixture: ComponentFixture<TrafficAlertResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TrafficAlertResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrafficAlertResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
