import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CommonService, EquipType } from '../../../service/common.service';
import { ReportsService } from '../reports.service';
import { EventManager } from '@angular/platform-browser';
import * as moment from 'moment';

@Component({
  selector: 'emas-technical-alert',
  templateUrl: './technical-alert.component.html',
  styleUrls: ['./technical-alert.component.css']
})
export class TechnicalAlertComponent implements OnInit {
  param: any;
  generate = true;
  alertTypeOne: any = [];
  statusOne: any = [];
  statu: any = '';
  fucos = [];
  alert = '';
  validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	listOfData = [];
  listOfData2 = [];
   // nz-date-picker
   startValue: Date | null = null;
   endValue: Date | null = null;
   endOpen = false;
   isVisibleMiddle = false;
	listSubSystem = [
	  ];
	listEquipID = [];
  listLocation: Array<{ label: string; value: number }> = [
		{ label: 'Site', value: 1},
		{ label: 'Backend', value: 2}
  ];
  listAckStatus: Array<{ label: string; value: number, ischecked: boolean }> = [
		{ label: 'False', value: 1, ischecked: false},
    { label: 'True', value: 0, ischecked: false},
    { label: 'Unverified', value: 2, ischecked: false}

  ];
  listExpWay = [];
  listEquipType = [];
  listAlarmDesc = [];
  equipid = [];
    reportValue = {
    startDate: null,
    endDate: null,
    equipType: null,
    alertType: null,
    equipID: null,
    expWay: null,
    withChart: null
  };
  expWayDisable = false;

	constructor(
    private fb: FormBuilder,
    private commonService: CommonService,
    private reportsService: ReportsService,
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
      location: [{ value: null, disabled: false }],
      expWay: [{ value: null, disabled: false }],
      equipType: [{ value: null, disabled: false }],
      alarmDsc: [{ value: null, disabled: false }],
      fromDate: [{ value: new Date(), disabled: false }],
      toDate: [{ value: new Date(), disabled: false }],
      withChart: [{vale: true, disabled: false}],
      ackStatus: [{vale: null, disabled: false}],
    });
  }
  fatchForm(): void  {
    this.validateForm.patchValue({
      subSystem: this.listSubSystem[0].value,
      equipType: this.listEquipType[0] ? this.listEquipType[0].value : '',
      expWay: this.listExpWay[0] ? this.listExpWay[0].value : '',
      alarmDsc: this.listAlarmDesc[0] ? this.listAlarmDesc[0].value : '',
      ackStatus: this.listAckStatus[0].value
    });
  }
	submitForm(): void {
    // console.log(this.validateForm);
    // console.log(this.listAlarmDesc)

    // console.log(this.listEquipID)
  }
  test(i, e) {
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
  alertType(i, e) {
    this.alertTypeOne = []
    if (e.ctrlKey) {
      i.ischecked = i.ischecked ? false : true;
    } else {
      this.listAlarmDesc.forEach(item => {
        item.ischecked = false;
        if (item === i) {
          item.ischecked = true;
        }
      });
    }
    this.listAlarmDesc.forEach(item => {
      if (item.ischecked) {
        this.alertTypeOne.push(item.value);
      }
    });
  }
  status(i, e) {
    this.statusOne = []
    if (e.ctrlKey) {
      i.ischecked = i.ischecked ? false : true;
    } else {
      this.listAckStatus.forEach(item => {
        item.ischecked = false;
        if (item === i) {
          item.ischecked = true;
        }
      });
    }
    this.listAckStatus.forEach(item => {
      if (item.ischecked) {
        this.statusOne.push(item.value);
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
    // if (this.validateForm.value.withChart) {
    //   console.log('true');
    // } else {
    //   console.log('false');
    // }
    const startDate = this.validateForm.value.fromDate;
    const endDate = this.validateForm.value.toDate;
    const equipType = this.validateForm.value.equipType;
    // const alertType = this.alertTypeOne.description;
    const equipID = this.validateForm.value.equipID;
    const expWay = this.validateForm.value.expWay;
    const withChart = this.validateForm.value.withChart;
    this.reportValue = {
      startDate: startDate,
      endDate: endDate,
      equipType: equipType,
      alertType: this.alertTypeOne,
      equipID: this.equipid,
      expWay: expWay,
      withChart: withChart
    };
    this.isVisibleMiddle = true;
    const data = {
      equipIdList: Array.from(new Set(this.equipid)),
      alertTypeList: Array.from(new Set(this.alertTypeOne)),
      ackStatusList: Array.from(new Set(this.statusOne)),
      fromDate: moment(startDate).format(),
      toDate: moment(endDate).format(),
      subSystem: this.validateForm.value.subSystem,
      equipType: this.validateForm.value.equipType,
      expressWay: this.validateForm.value.expWay
      // equipIdList: Array.from(new Set(this.equipIdList)),
      // fromDate: moment(startDate).format(),
      // toDate: moment(endDate).format(),
      // alarmDesc: this.alarmDescList,
      // subSystem: this.validateForm.value.subSystem,
      // equipType: this.validateForm.value.equipType,
      // propCodeList: propCodeList,
      // expwayCode: null
    };
    this.param = data;
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
    const location = this.validateForm.value.location;
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
          // } else if (item.expwayCode === expWay && item.subSystemId === data && item.equipType === equipType) { // equiptype筛选
            // if (item.expwayCode === expWay && item.subSystemId === data && item.equipType === equipType) {
            //   if (item.expwayCode === expWay && item.subSystemId === data && item.equipType === equipType) {
                  // this.listEquipID.push({label: item.equipId, value: item.equipId, ischecked: false});
            //     }
            // }
          }
        });
        this.listEquipID = this.listEquipID.sort((a, b) => a.label.localeCompare(b.label));
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

  // getEquipId(item) {
  //   this.listEquipID.push({label: item.equipId, value: item.equipId});
  // }

}