import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertAiComponent } from './alert-ai.component';

describe('AlertAiComponent', () => {
  let component: AlertAiComponent;
  let fixture: ComponentFixture<AlertAiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlertAiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlertAiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
