import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommandSendComponent } from './command-send.component';

describe('CommandSendComponent', () => {
  let component: CommandSendComponent;
  let fixture: ComponentFixture<CommandSendComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommandSendComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommandSendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
