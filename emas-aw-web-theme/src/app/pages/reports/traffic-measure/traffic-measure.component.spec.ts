import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficMeasureComponent } from './traffic-measure.component';

describe('TrafficMeasureComponent', () => {
  let component: TrafficMeasureComponent;
  let fixture: ComponentFixture<TrafficMeasureComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TrafficMeasureComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrafficMeasureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
