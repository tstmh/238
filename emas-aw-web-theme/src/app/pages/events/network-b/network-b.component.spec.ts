import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NetworkBComponent } from './network-b.component';

describe('NetworkBComponent', () => {
  let component: NetworkBComponent;
  let fixture: ComponentFixture<NetworkBComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NetworkBComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkBComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
