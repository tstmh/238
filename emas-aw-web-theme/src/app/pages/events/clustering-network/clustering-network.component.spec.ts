import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClusteringNetworkComponent } from './clustering-network.component';

describe('ClusteringNetworkComponent', () => {
  let component: ClusteringNetworkComponent;
  let fixture: ComponentFixture<ClusteringNetworkComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClusteringNetworkComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClusteringNetworkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
