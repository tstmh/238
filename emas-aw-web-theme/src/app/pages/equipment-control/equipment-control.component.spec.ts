import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipmentControlComponent } from './equipment-control.component';

describe('EquipmentControlComponent', () => {
  let component: EquipmentControlComponent;
  let fixture: ComponentFixture<EquipmentControlComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EquipmentControlComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EquipmentControlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
