import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FireAlarmPanelComponent } from './fire-alarm-panel.component';

describe('FireAlarmPanelComponent', () => {
  let component: FireAlarmPanelComponent;
  let fixture: ComponentFixture<FireAlarmPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FireAlarmPanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FireAlarmPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
