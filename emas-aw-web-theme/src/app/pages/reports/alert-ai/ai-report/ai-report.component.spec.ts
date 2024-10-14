import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AiReportComponent } from './ai-report.component';

describe('AiReportComponent', () => {
  let component: AiReportComponent;
  let fixture: ComponentFixture<AiReportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AiReportComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AiReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
