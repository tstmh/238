import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewLusComponent } from './view-lus.component';

describe('ViewLusComponent', () => {
  let component: ViewLusComponent;
  let fixture: ComponentFixture<ViewLusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewLusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewLusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
