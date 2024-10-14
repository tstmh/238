import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NetworkAComponent } from './network-a.component';

describe('NetworkAComponent', () => {
  let component: NetworkAComponent;
  let fixture: ComponentFixture<NetworkAComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NetworkAComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkAComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
