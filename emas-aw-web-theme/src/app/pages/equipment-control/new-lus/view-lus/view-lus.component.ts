import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { EquipmentService } from '../../equipment-control.service';
import { WebSocketService } from 'src/app/service/websocket.service';
import { DialogService } from 'src/app/share/dialog';
import { NzModalService } from 'ng-zorro-antd';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import * as moment from 'moment';

@Component({
  selector: 'emas-view-lus',
  templateUrl: './view-lus.component.html',
  styleUrls: ['./view-lus.component.css']
})

export class ViewLusComponent implements OnInit, AfterViewInit {
  @Input() selectItem: any;
  @Input() listOfData: any;
  @Input() equipType: any;
  @Input() isVisibleMiddle: boolean;
  @Output() closeModal = new EventEmitter();
  abortWebsocket$ = new Subject<void>();
  radioValue = '';
  newStatus = '';
  start = '';
  timerArr = [];
  timer : any;
  status = '';
  equipId = '';
  isSpinning = false;
  isApplyTemplateVisible = false;
  applyMessageWindow = '';
  attrCodeList = [];
  thisEquipType = '';
  dimmingList = [
    {
      description: 'Automatic',
      value: 'A'
    },
    {
      description: 'Force Day',
      value: 'D'
    },
    {
      description: 'Force Night',
      value: 'N'
    }];
  flashList = [
    {
      description: 'No Flash',
      value: '0'
    },
    {
      description: 'Flashing',
      value: '1'
    }];
  isFlash = '0';
  dimmingResult = 'A';
  dimmingLevel = '1';
  dimmingLevelList = [{label:'1',value:'1'},{label:'2',value:'2'},{label:'3',value:'3'},{label:'4',value:'4'},
    {label:'5',value:'5'},{label:'6',value:'6'},{label:'7',value:'7'},{label:'8',value:'8'},
    {label:'9',value:'9'},{label:'10',value:'10'},{label:'11',value:'11'},{label:'12',value:'12'}];
  flashingOnTime = 5;
  flashingOffTime = 5;
  commandStr = '';
  command = '';
  cmdValue = '';
  remoteList = ['wmv', 'wiv', 'jop', 'elp', 'cdt'];
  disabledMsg = true;
  disabled = false;
  enabled = false;
  alarmList: any = [];
  measureList: any = [];
  isEditable = true;
  tempExeId = '';
  imgMap = {
    0: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/blank.png',
    1: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/redCross.png',
    2: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/amberCross.png',
    3: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/greenArrow.png',
    4: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/downLeft.png',
    5: '../../../../../assets/img/Images/Screenshots_EquipmentControlTab/downRight.png'
  }

  constructor(
    private equipmentService: EquipmentService,
    private dialog: DialogService,
    private modal: NzModalService,
    private webSocketService: WebSocketService
  ) { }

  ngOnInit() {
    this.getSelectData();
    this.thisEquipType = this.selectItem.equipType;
    this.equipId = this.selectItem.equipId;
  }

  ngAfterViewInit(): void {
    let element = document.getElementsByClassName('ant-modal-header');
    //console.log('ngAfterViewInit', element)
    element[0].setAttribute('style', 'background-color: #83a5bd4f');
    element = document.getElementsByClassName('ant-modal-body');
    element[0].setAttribute('style', 'min-height: 400px');
  }

  getSelectData() {
    const queryData = {
      equipId: this.selectItem.equipId
    };
    // console.log('listofData: ', this.listOfData)
    this.equipmentService.getLUSEquipStatusByEquipId(queryData).subscribe(item => {
      // console.log('before selectData:', this.selectItem)
      // console.log('newData:', item)
      if (item.code == 0) {
        this.selectItem.attrValue = item.data.operationalState;
        this.selectItem = { ...this.selectItem, ...item.data };
        this.listOfData.forEach(ele => {
          if (ele.equipId == this.selectItem.equipId) {
            ele.attrValue = this.selectItem.attrValue;
          }
        });
        this.alarmList = item.data.technicalAlarmList;
        if (this.alarmList.length > 0) {
          this.alarmList.forEach(items => {
            items.startDate = moment(items.startDate).format('DD/MM/YYYY HH:mm:ss A');
          });
        }
      }
      this.dimmingList.forEach(ele => {
        if (this.selectItem.dimmingMode == ele.description) {
          this.dimmingResult = ele.value;
        }
      });
      this.radioValue = String(this.selectItem.currentGraphicsCode);
      console.log('comm selected:', this.selectItem);
      // console.log("dimmingResult:", this.dimmingResult, ", radioValue:", this.radioValue);
    });
    this.remoteList.forEach(e => {
      //console.log('equipType:',this.selectItem.equipType,',now equipType:',e)
      if (e == this.selectItem.equipType) {
        this.enabled = true;
      }
    });
    // console.log('comm selected:',this.selectItem)
    //console.log('attrList:',this.attrCodeList)
  }

  setMessageMode() {
    this.isApplyTemplateVisible = true;
    this.commandStr = "Massage";
    this.applyMessageWindow = 'Setting Massage';
    this.command = "cdd";
    // this.availableList = this.listDisplayEquipIDSearch;
  }
  setDimmingMode() {
    this.isApplyTemplateVisible = true;
    this.disabledMsg = false;
    this.commandStr = "Dimming";
    this.applyMessageWindow = 'Setting Dimming';
    this.command = "cdm";
    // this.availableList = this.listDisplayEquipIDSearch;
  }
  setFlashMode() {
    this.isApplyTemplateVisible = true;
    this.disabledMsg = false;
    this.commandStr = "Flash";
    this.applyMessageWindow = 'Setting Flash';
    this.command = "cdf";
    // this.availableList = this.listDisplayEquipIDSearch;
  }
  handleApplyModeCancel() {
    this.isApplyTemplateVisible = false;
    this.disabledMsg = true;
    this.commandStr = '';
    this.applyMessageWindow = '';
    this.isFlash = '0';
    this.refresh();
  }
  handleApplyModeOk() {
    // this.isApplyTemplateVisible = false;
    this.sendApplyMessage();
    this.disabled = true;
  }
  radioChange() {
    this.disabledMsg = false;
  }
  setDimmingLevel() {
    if (this.dimmingResult != 'A') {
      this.dimmingLevel = this.dimmingResult == 'D' ? '1' : '12';
    }
  }

  sendApplyMessage() {
    // console.log('isFlash:', this.isFlash)
    this.dialog.confirm({
      title: 'Confirmation Window',
      content: `Send ${this.commandStr} Command to ${this.selectItem.equipId}?`,
      buttonOkTxt: 'Yes(Y)',
      buttonCancelTxt: 'No(N)'
    }).subscribe(res => {
      if (res) {
        this.confirmSend(this.equipId);
        this.isSpinning = true;
        this.getSocket();
      } else {
        this.cancelSend();
      }
    });
    this.equipId = this.selectItem.equipId;
  }
  confirmSend(item) {
    let queryData = {};
    this.tempExeId = `AW_${localStorage.getItem('user_name')}_${moment().format('HHmmssSSS')}`;
    switch (this.commandStr) {
      case 'Massage':
        queryData = {
          lusMsg: {
            equipId: item,
            equipType: this.selectItem.equipType,
            attributeName: this.command,
            cmdValue: this.radioValue,
            sender: 'AW_user',
            execId: this.tempExeId,
            cmdId: '0'
          }
        };
        break;
      case 'Dimming':
        queryData = {
          lusMsg: {
            equipId: item,
            equipType: this.selectItem.equipType,
            attributeName: this.command,
            dimmingMode: this.dimmingResult,
            cmdValue: this.dimmingResult !== '0' ? this.dimmingLevel : 0,
            sender: 'AW_user',
            execId: this.tempExeId,
            cmdId: '0'
          }
        };
        break;
      case 'Flash':
        queryData = {
          lusMsg: {
            equipId: item,
            equipType: this.selectItem.equipType,
            attributeName: this.command,
            onTime: this.flashingOnTime,
            offTime: this.flashingOffTime,
            sender: 'AW_user',
            execId: this.tempExeId,
            cmdId: '0'
          }
        };
        break;
      default:
        break;
    }
    this.equipmentService.AW_CFELS_NEWLUS(this.commandStr, queryData);
    this.modalListInt();
  }
  modalListInt(): void {
    this.timer = setTimeout(() => {
      this.status = 'Time Out';
      this.timerArr = [];
      this.abortWebsocket$.next();
      this.isSpinning = false;
      this.disabled = false;
      this.showResponse();
    }, 12000);
    // this.timerArr.push(timer);
  }
  getSocket(): void {
    this.webSocketService.getMessage().pipe(takeUntil(this.abortWebsocket$.asObservable())).subscribe((msg: any) => {
      try {
        const response = JSON.parse(msg.data);
        console.log('response:', response)
        if (response.type !== 'HeartBeat') {
          if (this.equipId === response.EquipId && this.tempExeId === response.ExecId) {
            if (response.Status) {
              this.status = response.Message;
            } else {
              this.status = 'Time Out';
            }
            // clearTimeout(this.timerArr[0]);
            clearTimeout(this.timer);
            this.equipId = '';
            this.abortWebsocket$.next();
            this.isSpinning = false;
            this.disabled = false;
            this.showResponse();
          }
        }
      } catch (e) {
        console.warn('websocket process warn:', e);
      }
    });
  }

  showResponse() {
    // console.log('Now Status:', this.status)
    if (this.status == 'Success') {
      this.modal.success({ nzTitle: 'Send Success', nzContent: `Send ${this.commandStr} Command ${this.status}.` });
    } else {
      this.modal.error({ nzTitle: 'Send Failed', nzContent: `Send ${this.commandStr} Command ${this.status}.` });
    }
  }
  cancelSend() {
    // console.log("Cancel");
    this.disabled = false;
  }

  refresh() {
    // this.radioValue = '';
    this.getSelectData();
  }

  close() {
    this.isVisibleMiddle = false;
    this.closeModal.emit(true);
    this.selectItem.active = false;
  }
}
