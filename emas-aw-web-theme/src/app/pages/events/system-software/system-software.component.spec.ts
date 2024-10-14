import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemSoftwareComponent } from './system-software.component';

describe('SystemSoftwareComponent', () => {
  let component: SystemSoftwareComponent;
  let fixture: ComponentFixture<SystemSoftwareComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SystemSoftwareComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SystemSoftwareComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
