import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VmsControlComponent } from './vms-control.component';

describe('VmsControlComponent', () => {
  let component: VmsControlComponent;
  let fixture: ComponentFixture<VmsControlComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VmsControlComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VmsControlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
