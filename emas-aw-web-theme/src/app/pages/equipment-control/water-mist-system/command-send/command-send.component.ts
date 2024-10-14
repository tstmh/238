import { Component, OnInit, Input, Output, EventEmitter, ViewChild, AfterViewInit } from '@angular/core';
import { ApplyCommandComponent } from '../apply-command/apply-command.component';
import { EquipmentService } from '../../equipment-control.service';
import * as moment from 'moment';

@Component({
  selector: 'emas-command-send',
  templateUrl: './command-send.component.html',
  styleUrls: ['./command-send.component.css']
})
export class CommandSendComponent implements OnInit, AfterViewInit {
  @ViewChild(ApplyCommandComponent) private childcomponent: ApplyCommandComponent;
  @Input() selectItem: any;
  @Input() listOfData: any;
  @Input() equipType: any;
  @Input() isVisibleMiddle: boolean;
  @Output() closeModal = new EventEmitter();
  radioValue = '';
  newStatus = '';
  start = '';
  disabled: boolean;
  attrCodeList = [];
  thisEquipType = '';
  remoteList = ['wmv', 'wiv', 'jop', 'elp', 'cdt'];
  enabled = false;
  alarmList: any = [];
  measureList: any = [];

  constructor(
    private equipmentService: EquipmentService,
  ) { }

  ngOnInit() {
    // if (this.selectItem.attrValue == 2) {
    //   this.newStatus = 'Operational';
    // } else {
    //   this.newStatus = 'Not Operational';
    // }
    if (this.radioValue === '') {
      this.disabled = true;
    } else {
      this.disabled = false;
    }
    this.getSelectData();
    this.thisEquipType = this.selectItem.equipType;
    //this.changeModal();
  }
  ngAfterViewInit(): void {
    let element = document.getElementsByClassName('ant-modal-header');
    //console.log('ngAfterViewInit', element)
    element[0].setAttribute('style', 'background-color: #83a5bd4f');
    element = document.getElementsByClassName('ant-modal-body');
    element[0].setAttribute('style', 'min-height: 400px');
  }

  changeModal() {
    //console.log('equipType:',this.selectItem.equipType)    
    switch (this.selectItem.equipType) {
      case 'cdt': {
        //this.stat = true;
        break;
      }
      case 'dep': {
        //this.doos = true;
        break;
      }
      case 'fcp': {
        //this.venl = true;
        //this.venle = true;
        break;
      }
      case 'jop': {
        //this.doos = true;
        break;
      }
      case 'lcp': {
        //this.doos = true;
        break;
      }
      case 'mcp': {
        //this.doos = true;
        break;
      }
      case 'wiv': {
        //this.doos = true;
        break;
      }
      case 'wmp': {
        //this.doos = true;
        break;
      }
      case 'wmr': {
        //this.doos = true;
        break;
      }
      case 'wmt': {
        //this.doos = true;
        break;
      }
      case 'wmv': {
        // this.ope = true;
        // this.sti = true;
        // this.sto = true;
        break;
      }
    }
  }

  getSelectData() {
    const queryData = {
      equipId: this.selectItem.equipId
    };
    this.equipmentService.getWMSEquipStatusByEquipId(queryData).subscribe(item => {
      // console.log('selectedData:', item)
      if (item.code == 0) {
        item.data.currentStatus.forEach(data => {
          if (data.equipId == this.selectItem.equipId) {
            let str = data.attrCode + 'Value';
            this.selectItem[str] = data.attrValue;
            this.attrCodeList.push(data);
          }
        });
        this.listOfData.forEach(ele => {
          if (ele.equipId == this.selectItem.equipId) {
            ele.attrValue = this.selectItem.opeValue;
          }
        });
        this.alarmList = item.data.technicalAlarmList;
        this.alarmList.forEach(items => {
          items.startDate = moment(items.startDate).format('DD/MM/YYYY HH:mm:ss A');
        });
        this.measureList = item.data.measureStatusList;
        this.measureList.forEach((ele, idx) => {
          this.measureList[idx]['measureDescription'] = ele.equipMeasureConfig.measureDescription;
        });
        //console.log('measureList:', this.measureList)
      }
    });
    this.remoteList.forEach(e => {
      //console.log('equipType:',this.selectItem.equipType,',now equipType:',e)
      if (e == this.selectItem.equipType) {
        this.enabled = true;
      }
    });
    //console.log('comm selected:',this.selectItem)
    //console.log('attrList:',this.attrCodeList)
  }
  radioChange() {
    this.disabled = false;
  }

  apply(): void {
    this.childcomponent.showModal();
  }

  clear(): void {
    this.radioValue = '';
    this.disabled = true;
  }

  refresh() {
    this.clear();
    this.getSelectData();
  }

  close() {
    this.isVisibleMiddle = false;
    this.closeModal.emit(true);
    this.selectItem.active = false;
  }
}
