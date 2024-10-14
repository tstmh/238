import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CommonService, EquipType } from '../../../service/common.service';
import { ReportsService } from '../reports.service';
import * as moment from 'moment';
import * as _ from 'lodash';

@Component({
  selector: 'emas-technical-alarm',
  templateUrl: './technical-alarm.component.html',
  styleUrls: ['./technical-alarm.component.css']
})
export class TechnicalAlarmComponent implements OnInit {
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
  reportDisable = true;
  listSubSystem = [
    { label: 'All', value: 'All' },
  ];
  listEquipID = [{ label: 'All', value: 'All', ischecked: true }];
  listLocation: Array<{ label: string; value: number }> = [
    { label: 'Site', value: 0 },
    { label: 'Backend', value: 1 }
  ];
  listExpWay = [];
  listEquipType = [
    { label: 'All', value: 'All' },
    { label: 'bcw', value: 'bcw' },
    { label: 'dtt', value: 'dtt' },
    { label: 'psc', value: 'psc' },
    { label: 'tep', value: 'tep' },
    { label: 'tip', value: 'tip' },
    { label: 'tsc', value: 'tsc' },
    { label: 'tsp', value: 'tsp' },
    { label: 'ttp', value: 'ttp' },
    { label: 'wec', value: 'wec' }
  ];
  listEquipType2 = [
    { label: 'All', value: 'All' },
    { label: 'bcw', value: 'bcw' },
    { label: 'dtt', value: 'dtt' },
    { label: 'psc', value: 'psc' },
    { label: 'tep', value: 'tep' },
    { label: 'tip', value: 'tip' },
    { label: 'tsc', value: 'tsc' },
    { label: 'tsp', value: 'tsp' },
    { label: 'ttp', value: 'ttp' },
    { label: 'wec', value: 'wec' }
  ];
  listAlarmDesc = [
    { label: 'All', value: 'All', ischecked: true }
  ];
  reportValue = {
    startDate: null,
    endDate: null,
    equipType: null,
    alarmDsc: null,
    equipID: null,
    expWay: null,
    withChart: null
  };
  expWayDisable = false;
  equipIdList = [];
  alarmDescList = [];
  AlarmReportParam: any;

  constructor(
    private fb: FormBuilder,
    private commonService: CommonService,
    private reportsService: ReportsService
  ) { }

  ngOnInit() {
    this.initForm();
    // this.fatchForm();
    this.initData();
  }

  initForm() {
    this.validateForm = this.fb.group({
      subSystem: [{ value: null, disabled: false }],
      equipID: [{ value: null, disabled: false }],
      location: [{ value: null, disabled: false }],
      expWay: [{ value: null, disabled: false }],
      equipType: [{ value: 'All', disabled: false }],
      alarmDsc: [{ value: 'All', disabled: false }],
      fromDate: [{ value: new Date(), disabled: false }],
      toDate: [{ value: new Date(), disabled: false }],
      withChart: [{ vale: null, disabled: false }]
    });
  }

  // fatchForm(): void {
  //   this.validateForm.patchValue({
  //     subSystem: this.listSubSystem[0].value,
  //     equipType: this.listEquipType[0] ? this.listEquipType[0].value : '',
  //     expWay: this.listExpWay[0] ? this.listExpWay[0].value : '',
  //     location: this.listLocation[0].value,
  //     withChart: true
  //   });
  //   this.reportsService.getAllEquipConfig().subscribe((resData) => {
  //     const expWay = this.validateForm.value.expWay;
  //     resData.forEach((item) => {
  //       if (item.propertyCode == 0 && item.expwayCode == expWay) {
  //         this.listEquipID.push({ label: item.equipId, value: item.equipId, ischecked: false });
  //       }
  //     });
  //     // tslint:disable-next-line: max-line-length
  //     this.listEquipID = this.listEquipID.length === 1 ? [{ label: '--No Data--', value: '--No Data--', ischecked: false }] : this.listEquipID;
  //     this.reportDisable = this.listEquipID.length > 1 ? false : true;
  //   });
  // }
  submitForm(): void {
    // console.log(this.validateForm);
  }
  // nz-date-picker
  disabledStartDate = (startValue: Date): boolean => {
    if (!startValue || !this.endValue) {
      return false;
    }
    return startValue.getTime() > this.endValue.getTime();
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
    const alarmDescription = [];
    this.listAlarmDesc.forEach(item => {
      this.alarmDescList.forEach(item2 => {
        if (item.value == item2) {
          alarmDescription.push(item.label);
        }
      });
    });
    const { fromDate, toDate, equipType, expWay, alarmDsc, location, withChart, subSystem } = this.validateForm.value;
    const equipID = this.equipIdList;
    this.isVisibleMiddle = true;
    const propCodeList = location ? [1, 2] : [0];
    this.reportValue = {
      startDate: fromDate,
      endDate: toDate,
      equipType,
      alarmDsc,
      equipID,
      expWay,
      withChart: withChart
    };
    const data = {
      equipIdList: this.equipIdList,
      fromDate: moment(fromDate).format(),
      toDate: moment(toDate).format(),
      alarmDesc: this.alarmDescList,
      subSystem,
      equipType,
      propCodeList: propCodeList,
      expwayCode: expWay
    };
    if (this.equipIdList.length) {
      this.equipIdList.forEach(item => {
        if (item === 'All') {
          // console.log(123);
          delete data.equipIdList;
        }
      });
    } else {
      delete data.equipIdList;
    }
    if (this.alarmDescList.length) {
      this.alarmDescList.forEach(item => {
        if (item === 'All') {
          delete data.alarmDesc;
        }
      });
    } else {
      delete data.alarmDesc;
    }
    if (equipType === 'All') {
      // console.log(456);
      delete data.equipType;
    }
    if (subSystem === 'All') {
      delete data.subSystem;
    }
    // console.log(data);
    this.AlarmReportParam = data;
    // this.reportsService.getHistTechAlarm(data).subscribe((r) => {
    //   console.log(r);
    // });
  }

  closeModal() {
    this.isVisibleMiddle = false;
  }
  async initData() {
    const allType = await this.commonService.allType;
    const expway = allType[EquipType.EXPWAY_CODE];
    const subSystem = allType[EquipType.EMAS_SUBSYSTEM];
    subSystem.forEach((item) => {
      this.listSubSystem.push({ label: item.value, value: item.value });
    });
    expway.sort((a, b) => a.description.localeCompare(b.description)).forEach((item) => {
      this.listExpWay.push({ label: item.description, value: item.value });
    });
    this.validateForm.patchValue({
      subSystem: this.listSubSystem[0].value,
      equipType: this.listEquipType[0] ? this.listEquipType[0].value : '',
      expWay: this.listExpWay[0] ? this.listExpWay[0].value : '',
      location: this.listLocation[0].value,
      withChart: true
    });
    this.getEuipTypeBySubSystemAndLocation();
  }

  getEuipTypeBySubSystemAndLocation() {

    this.reportsService.getAllEquipConfig().subscribe(newData => {
      const { subSystem, equipType, location, expWay } = this.validateForm.value;
      this.listEquipID = [];
      const equipTypeSet = new Set();
      newData.forEach(item => {
        if (location === 1) { // 'Backend'
          if ((item.propertyCode == 1 || item.propertyCode == 2)
            && (item.subSystemId == subSystem || subSystem === 'All')) {
            equipTypeSet.add(item.equipType);
            if (equipType === 'All' || equipType === item.equipType) {
              this.listEquipID.push({ label: item.equipId, value: item.equipId, 'ischecked': false });
            }
          }
        } else if (item.propertyCode == 0
          && (item.subSystemId == subSystem || subSystem === 'All')) {
          equipTypeSet.add(item.equipType);
          if ((equipType === 'All' || equipType === item.equipType)
            && item.expwayCode == expWay) {
            this.listEquipID.push({ label: item.equipId, value: item.equipId, 'ischecked': false });
          }
        }
      });
      this.listEquipType = [...equipTypeSet].sort().map((e: any) => { return { label: e, value: e } });


      if (this.listEquipID.length > 0) { // 默认选中 all
        this.listEquipID.unshift({ label: 'All', value: 'All', 'ischecked': true });
        this.equipIdList = ['All'];
        this.reportDisable = false;
      } else {
        this.listEquipID = [{ label: '--No Data--', value: '--No Data--', ischecked: false }]
        this.reportDisable = true;
      }
      if (this.listEquipType.length > 0) {
        this.listEquipType.unshift({ 'label': 'All', value: 'All' });
      }
    });

  }

  getAlarmDescriptionsByEquipType() {

    this.reportsService.getAllTechAlarmConfig().subscribe((r) => { // 获取alarmdesc
      const equipType = this.validateForm.value.equipType;
      const res: any = r;
      const resData = res.Body.getAllTechAlarmConfigResponse.technicalAlarmConfigList;
      this.listAlarmDesc = [{ label: 'All', value: 'All', ischecked: true }];
      resData.forEach((item) => {
        if (item.equipType == equipType) {
          this.listAlarmDesc.push({ label: item.description, value: item.alarmCode, ischecked: false });
        }
      });
      this.listAlarmDesc = _.uniqBy(this.listAlarmDesc, 'value');
      if (this.listAlarmDesc.length === 1 && equipType !== 'All') {
        this.listAlarmDesc = [{ label: '-- No Data--', value: '-1', ischecked: true }];
      }
    });

  }



  equipTypeChange() {
    this.getEuipTypeBySubSystemAndLocation();
    this.getAlarmDescriptionsByEquipType();
  }
  locationChange() {
    const { location } = this.validateForm.value;
    if (location === 1) {
      this.expWayDisable = true;
    } else {
      this.expWayDisable = false;
    }
    this.getEuipTypeBySubSystemAndLocation();
  }
  equipIdactive(i, e) {
    this.equipIdList = [];
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
        this.equipIdList.push(item.value);
      }
    });
  }

  alarmDescActive(i, e) {
    this.alarmDescList = [];
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
        this.alarmDescList.push(item.value);
      }
    });
  }

}
