import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPictogramComponent } from './add-pictogram.component';

describe('AddPictogramComponent', () => {
  let component: AddPictogramComponent;
  let fixture: ComponentFixture<AddPictogramComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddPictogramComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddPictogramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
