import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PmcsComponent } from './pmcs.component';

describe('PmcsComponent', () => {
  let component: PmcsComponent;
  let fixture: ComponentFixture<PmcsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PmcsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PmcsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
