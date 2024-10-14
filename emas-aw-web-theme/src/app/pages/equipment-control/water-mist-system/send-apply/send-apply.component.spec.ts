import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SendApplyComponent } from './send-apply.component';

describe('SendApplyComponent', () => {
  let component: SendApplyComponent;
  let fixture: ComponentFixture<SendApplyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SendApplyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SendApplyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
