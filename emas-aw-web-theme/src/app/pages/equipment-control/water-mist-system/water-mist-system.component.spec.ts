import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterMistSystemComponent } from './water-mist-system.component';

describe('WaterMistSystemComponent', () => {
  let component: WaterMistSystemComponent;
  let fixture: ComponentFixture<WaterMistSystemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WaterMistSystemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterMistSystemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
