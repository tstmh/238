import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstrusionDetectionComponent } from './instrusion-detection.component';

describe('InstrusionDetectionComponent', () => {
  let component: InstrusionDetectionComponent;
  let fixture: ComponentFixture<InstrusionDetectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstrusionDetectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstrusionDetectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
