import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PictogramComponent } from './pictogram.component';

describe('PictogramComponent', () => {
  let component: PictogramComponent;
  let fixture: ComponentFixture<PictogramComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PictogramComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PictogramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
