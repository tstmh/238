import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TechnicalAlertComponent } from './technical-alert.component';

describe('TechnicalAlertComponent', () => {
  let component: TechnicalAlertComponent;
  let fixture: ComponentFixture<TechnicalAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TechnicalAlertComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TechnicalAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
