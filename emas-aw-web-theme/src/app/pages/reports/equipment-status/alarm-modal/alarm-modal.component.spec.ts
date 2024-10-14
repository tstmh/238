import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlarmModalComponent } from './alarm-modal.component';

describe('AlarmModalComponent', () => {
  let component: AlarmModalComponent;
  let fixture: ComponentFixture<AlarmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlarmModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlarmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
