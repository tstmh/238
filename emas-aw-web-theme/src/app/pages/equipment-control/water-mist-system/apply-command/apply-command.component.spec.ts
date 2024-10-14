import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplyCommandComponent } from './apply-command.component';

describe('ApplyCommandComponent', () => {
  let component: ApplyCommandComponent;
  let fixture: ComponentFixture<ApplyCommandComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplyCommandComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplyCommandComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
