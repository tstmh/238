import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewLusComponent } from './new-lus.component';

describe('NewLusComponent', () => {
  let component: NewLusComponent;
  let fixture: ComponentFixture<NewLusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewLusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewLusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
