import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipmentHousingComponent } from './equipment-housing.component';

describe('EquipmentHousingComponent', () => {
  let component: EquipmentHousingComponent;
  let fixture: ComponentFixture<EquipmentHousingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EquipmentHousingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EquipmentHousingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
