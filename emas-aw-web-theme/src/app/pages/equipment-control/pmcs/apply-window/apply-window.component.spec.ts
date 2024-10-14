import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplyWindowComponent } from './apply-window.component';

describe('ApplyWindowComponent', () => {
  let component: ApplyWindowComponent;
  let fixture: ComponentFixture<ApplyWindowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplyWindowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplyWindowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
