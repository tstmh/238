import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicModalComponent } from './public-modal.component';

describe('PublicModalComponent', () => {
  let component: PublicModalComponent;
  let fixture: ComponentFixture<PublicModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PublicModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PublicModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
