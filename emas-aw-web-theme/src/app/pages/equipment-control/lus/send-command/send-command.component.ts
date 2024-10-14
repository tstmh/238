import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit, ViewChild } from '@angular/core';
import { ApplyModalComponent } from '../apply-modal/apply-modal.component';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'emas-send-command',
  templateUrl: './send-command.component.html',
  styleUrls: ['./send-command.component.css']
})
export class SendCommandComponent implements OnInit, AfterViewInit {
  @ViewChild(ApplyModalComponent) private childcomponent: ApplyModalComponent;
  @Input() selectItem: any;
  @Input() listOfData: any;
  @Input() isVisibleMiddle: boolean;
  @Output() closeModal = new EventEmitter();
  radioValue = '';
  newStatus = '';
  disabled: boolean;

  imgMap = {
    0: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/greenArrow.png',
    1: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/redCross.png',
    2: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/amberCross.png',
    3: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/blank.png'
  }
  constructor() { }

  ngOnInit() {
    this.radioChange();
    if (this.selectItem.attrValue === '2') {
      this.newStatus = 'Operational';
    } else {
      this.newStatus = 'Not Operational';
    }
    if (this.radioValue === '') {
      this.disabled = true;
    } else {
      this.disabled = false;
    }
  }
  ngAfterViewInit() {
    const button: any = document.querySelector('.ant-modal-close'); // 重置弹出框删除图标总是显示边框问题
    button.style.outline = 'unset';
  }
  radioChange() {
    this.disabled = false;
  }
  handleOkMiddle(): void {
    this.childcomponent.showModal();
  }

  handleCancelMiddle(): void {
    this.radioValue = '';
    this.disabled = true;
  }
  close() {
    this.isVisibleMiddle = false;
    this.closeModal.emit(true);
    this.selectItem.active = false;
  }

}
