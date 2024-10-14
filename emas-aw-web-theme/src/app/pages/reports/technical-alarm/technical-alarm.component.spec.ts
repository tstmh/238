import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TechnicalAlarmComponent } from './technical-alarm.component';

describe('TechnicalAlarmComponent', () => {
  let component: TechnicalAlarmComponent;
  let fixture: ComponentFixture<TechnicalAlarmComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TechnicalAlarmComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TechnicalAlarmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
