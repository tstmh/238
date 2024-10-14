import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FelsParametersComponent } from './fels-parameters.component';

describe('FelsParametersComponent', () => {
  let component: FelsParametersComponent;
  let fixture: ComponentFixture<FelsParametersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FelsParametersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FelsParametersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
