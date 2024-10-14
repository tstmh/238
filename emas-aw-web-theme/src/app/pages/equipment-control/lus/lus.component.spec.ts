import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LusComponent } from './lus.component';

describe('LusComponent', () => {
  let component: LusComponent;
  let fixture: ComponentFixture<LusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
