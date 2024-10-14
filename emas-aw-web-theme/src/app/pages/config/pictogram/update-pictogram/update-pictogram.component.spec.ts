import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatePictogramComponent } from './update-pictogram.component';

describe('UpdatePictogramComponent', () => {
  let component: UpdatePictogramComponent;
  let fixture: ComponentFixture<UpdatePictogramComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdatePictogramComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdatePictogramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
