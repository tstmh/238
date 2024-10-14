import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadPictogramComponent } from './download-pictogram.component';

describe('DownloadPictogramComponent', () => {
  let component: DownloadPictogramComponent;
  let fixture: ComponentFixture<DownloadPictogramComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DownloadPictogramComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadPictogramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
