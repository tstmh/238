import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CommonService, EquipType } from '../../../service/common.service';
import { ReportsService } from '../reports.service';
import { EventManager } from '@angular/platform-browser';
import { NzModalService } from 'ng-zorro-antd/modal';
import * as moment from 'moment';

@Component({
  selector: 'emas-alert-ai',
  templateUrl: './alert-ai.component.html',
  styleUrls: ['./alert-ai.component.css']
})
export class AlertAiComponent implements OnInit {
  param: any;
  generate = true;
  reslutTypeOne: any = [];
  statusOne: any = [];
  statu: any = '';
  fucos = [];
  alert = '';
  validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	listOfData = [];
  listOfData2 = [];
  startValue: Date | null = null;
  endValue: Date | null = null;
  endOpen = false;
  isVisibleMiddle = false;
	listSubSystem = [
	  ];
	listEquipID = [];
  listAIType: Array<{ label: string; value: string }> = [
		{ label: 'Video', value: 'video'},
		{ label: 'Image', value: 'image'}
  ];
  listResultType: Array<{ label: string; value: string, ischecked: boolean }> = [
    { label: 'All', value: 'all', ischecked: false},
    { label: 'True', value: 'true', ischecked: false},
		{ label: 'False', value: 'false', ischecked: false}
  ];
  listExpWay = [];
  listEquipType = [];
  listAlarmDesc = [];
  equipid = [];
  reportValue = {
    startDate: null,
    endDate: null,
    equipType: null,
    resultType: null,
    AIType: null,
    equipID: null,
    expWay: null,
    withChart: null
  };
  expWayDisable = false;

	constructor(
    private fb: FormBuilder,
    private commonService: CommonService,
    private reportsService: ReportsService,
    private modal: NzModalService,
    private eventManager: EventManager
    ) { }

	ngOnInit() {
    this.initForm();
    this.initData();
  }
 
	initForm() {
		this.validateForm = this.fb.group({
			subSystem: [{ value: null, disabled: false }],
      equipID: [{ value: null, disabled: false }],
      expWay: [{ value: null, disabled: false }],
      equipType: [{ value: null, disabled: false }],
      AIType: [{ value: null, disabled: false }],
      fromDate: [{ value: new Date(), disabled: false }],
      toDate: [{ value: new Date(), disabled: false }],
      withChart: [{vale: true, disabled: false}],
      resultType: [{vale: null, disabled: false}],
    });
  }
  fatchForm(): void  {
    this.validateForm.patchValue({
      subSystem: this.listSubSystem[0].value,
      equipType: this.listEquipType[0] ? this.listEquipType[0].value : '',
      expWay: this.listExpWay[0] ? this.listExpWay[0].value : '',
      AIType: this.listAIType[0] ? this.listAIType[0].value : '',
      resultType: this.listResultType[0].value
    });
  }
	submitForm(): void {
    // console.log(this.validateForm);
    // console.log(this.listAlarmDesc)

    // console.log(this.listEquipID)
  }
  selectEquip(i, e) {
    this.equipid = []
    if (e.ctrlKey) {
      i.ischecked = i.ischecked ? false : true;
    } else {
      this.listEquipID.forEach(item => {
        item.ischecked = false;
        if (item === i) {
          item.ischecked = true;
        }
      });
    }
    this.listEquipID.forEach(item => {
      if (item.ischecked) {
        this.equipid.push(item.value);
      }
    });
  }
  reslutType(i, e) {
    this.reslutTypeOne = []
    if (e.ctrlKey) {
      i.ischecked = i.ischecked ? false : true;
    } else {
      this.listResultType.forEach(item => {
        item.ischecked = false;
        if (item === i) {
          item.ischecked = true;
        }
      });
    }
    this.listResultType.forEach(item => {
      if (item.ischecked) {
        this.reslutTypeOne.push(item.value);
      }
    });
  }
  // nz-date-picker
  disabledStartDate = (startValue: Date): boolean => {
    if (!startValue || !this.endValue) {
      return false;
    }
    return startValue.getTime() > this.endValue.getTime();
  }
  onEnter(e) {
   // console.log(e)
  }
  disabledEndDate = (endValue: Date): boolean => {
    if (!endValue || !this.startValue) {
      return false;
    }
    return endValue.getTime() <= this.startValue.getTime();
  }

  onStartChange(date: Date): void {
    this.startValue = date;
  }

  onEndChange(date: Date): void {
    this.endValue = date;
  }

  handleStartOpenChange(open: boolean): void {
    if (!open) {
      this.endOpen = true;
    }
    // console.log('handleStartOpenChange', open, this.endOpen);
  }

  handleEndOpenChange(open: boolean): void {
    // console.log(open);
    this.endOpen = open;
  }

  showModalMiddle(): void {
    const startDate = this.validateForm.value.fromDate;
    const endDate = this.validateForm.value.toDate;
    const equipType = this.validateForm.value.equipType;
    const expWay = this.validateForm.value.expWay;
    const withChart = this.validateForm.value.withChart;
    const aiType = this.validateForm.value.AIType;
    if (this.reslutTypeOne.length > 1){
      this.reslutTypeOne = ["all"];
    }
    this.reportValue = {
      startDate: startDate,
      endDate: endDate,
      equipType: equipType,
      equipID: this.equipid,
      resultType: this.reslutTypeOne,
      AIType: aiType,
      expWay: expWay,
      withChart: withChart
    };
    // console.log(this.reportValue);
    if (this.reslutTypeOne.length == 0 || this.equipid.length == 0){
      this.modal.warning({
        nzTitle: 'Warning',
        nzContent: 'Please select Result Type and Equip ID.'
      });
      return;
    }
    if (expWay === 'all' && (endDate - startDate) > 86400000*3){
      this.modal.warning({
        nzTitle: 'Warning',
        nzContent: 'Please select the Date less than 3 days when Exp Way is All.'
      });
      return;
    }
    if (aiType == 'image') {
      if (this.reslutTypeOne[0] != 'all'){
        this.reslutTypeOne[0] = this.reslutTypeOne[0] == 'true' ? '1' : '0';
      }
    }
    this.isVisibleMiddle = true;
    const data = {
      equipIdList: Array.from(new Set(this.equipid)),
      resultType: this.reslutTypeOne[0],
      AIType: this.validateForm.value.AIType,
      fromDate: moment(startDate).format(),
      toDate: moment(endDate).format(),
      subSystem: this.validateForm.value.subSystem,
      equipType: this.validateForm.value.equipType,
      expressWay: this.validateForm.value.expWay
    };
    this.param = data;
    // console.log(this.param);
  }

	closeModal() {
		this.isVisibleMiddle = false;
  }
  async initData() {
    const allType = await this.commonService.allType;
    const expway = allType[EquipType.EXPWAY_CODE];
    const subSystem = allType[EquipType.EMAS_SUBSYSTEM];
    subSystem.forEach((item) => {
      if (item.description === 'Detection Camera SubSystem') {
        this.listSubSystem.push({label: item.value, value: item.value});
        return;
      }
    });
    expway.forEach((item) => {
      this.listExpWay.push({label: item.description, value: item.value});
    });
    this.listExpWay.unshift({label: 'All', value: 'all'});
    // console.log(this.listExpWay);
    this.fatchForm();
    this.getEquipType(allType[EquipType.ALERT_TYPE]);
  }
  getEuipTypeBySubSystemAndLocation() {
  }
  getEquipType(listAlertType) {
    this.listAlarmDesc = listAlertType;
    this.listAlarmDesc.forEach((item, i) => {
      item.ischecked = false;
    });
    const data = this.validateForm.value.subSystem;
    this.listEquipID = [];
    this.reportsService.getEquipTypeBySubSytem(data).subscribe((r) => {
      const res: any = r;
      const resData = res.Body.getEquipTypeBySubSytemResponse.commonTypeConfigList;
      const listData = resData ? [...resData] : [];
      this.listEquipType = listData;
      this.fatchForm();
      this.getEquipIdByexpWayAndEquipType();
    });
  }
  getAlarmDescriptionsByEquipType() {
    const equipType = this.validateForm.value.equipType;
    const data = this.validateForm.value.subSystem;
    this.listEquipID = [];
  }

  getEquipIdByexpWayAndEquipType() {
    const data = this.validateForm.value.subSystem;
    const location = this.validateForm.value.location;
    const expWay = this.validateForm.value.expWay;
    const equipType = this.validateForm.value.equipType;
   
      const equipConfigDtoList = [];
      this.reportsService.getAllEquipConfig().subscribe((resData) => {
        this.listEquipID = [];
        resData.forEach((item) => {
          if (item.expwayCode !== undefined) {
            equipConfigDtoList.push(item);
          }
        });
        equipConfigDtoList.forEach((item) => {
          if (item.expwayCode === expWay && item.subSystemId === data && (!equipType ||  item.equipType === equipType )) { // 全部equipid值
            this.listEquipID.push({label: item.equipId, value: item.equipId, ischecked: false});
          }
        });
        this.listEquipID = this.listEquipID.sort((a, b) => a.label.localeCompare(b.label));
        this.listEquipID.unshift({label: 'All', value: 'all', ischecked: false});
        // console.log(this.listEquipID)
        if (this.listEquipID.length === 0) {
          this.generate = true;
          // console.log(this.listEquipID)
        } else {
          this.generate = false;
         //  console.log(this.listEquipID)
        }
      });
  }

}