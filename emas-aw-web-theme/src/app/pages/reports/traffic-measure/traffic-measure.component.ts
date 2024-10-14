import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ReportsService } from '../reports.service';
import { CommonService, EquipType } from '../../../service/common.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import * as moment from 'moment';


@Component({
  selector: 'emas-traffic-measure',
  templateUrl: './traffic-measure.component.html',
  styleUrls: ['./traffic-measure.component.css']
})
export class TrafficMeasureComponent implements OnInit {

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
  // activeEquiId: any;
  activeLi = {
    activeEquiId: null,
    activeMeasureType: 1,
    activeLaneType: 1
  };
  reportDisable = true;
  listSubSystem = [
    { label: 'dcss', value: 'dcss' }
  ];
  listExpWay = [];
  listEquipType = [];
  listMeasureType = [
    { label: 'Lane', value: 1, ischecked: true },
    { label: 'Carriageway', value: 0, ischecked: false }
  ];
  listLaneType = [
    { label: 'Normal Lane', value: 1, ischecked: true },
    { label: 'Entrance', value: 2, ischecked: false },
    { label: 'Exit', value: 3, ischecked: false },
  ];
  listEquipId = [];
  listInterval = [
    { label: '1 Minute', value: 1 },
    { label: '5 Minutes', value: 2 },
    { label: '1 Hour', value: 3 },
  ];
  reportValue = {
    startDate: null,
    endDate: null,
    interval: null,
    laneTypeList: null,
    equipID: null,
    expWay: null
  };
  equipIdList = [];
  laneTypeList = [];
  param: any;
  expWayDescription: any;
  laneTypeDescription = [];
  intervalDescription: any;

  constructor(
    private fb: FormBuilder,
    private commonService: CommonService,
    private reportsService: ReportsService,
    private message: NzMessageService
  ) { }

  ngOnInit() {
    this.initForm();

    this.initData();

    // console.log(this.validateForm);
  }

  initForm() {
    this.validateForm = this.fb.group({
      subSystem: [{ value: null, disabled: false }],
      expWay: [{ value: null, disabled: false }],
      equipType: [{ value: null, disabled: false }],
      fromDate: [{ value: new Date(), disabled: false }],
      toDate: [{ value: new Date(), disabled: false }],
      interval: [{ value: null, disabled: false }],
      withChart: [{ vale: null, disabled: false }]
    });
  }
  async initData() {
    const allType = await this.commonService.allType;
    const expway = allType[EquipType.EXPWAY_CODE];
    expway.forEach((item) => {
      this.listExpWay.push({ label: item.description, value: item.value });
    });
    this.getEquipTypeBySubstem();
    this.fatchForm();
  }

  fatchForm(): void {
    this.validateForm.patchValue({
      subSystem: this.listSubSystem[0].value,
      equipType: this.listEquipType[0] ? this.listEquipType[0].value : 'dtt',
      expWay: this.listExpWay[0] ? this.listExpWay[0].value : '',
      interval: this.listInterval[0].value,
      withChart: true
    });
    this.getEquipIdByExpway();
  }

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

  getEquipTypeBySubstem() {

    const subSystem = this.listSubSystem[0].value;
    const subSystem1 = this.validateForm.value.subSystem;
    const subSystem2 = subSystem1 == null ? subSystem : subSystem1;
    this.reportsService.getEquipTypeBySubSytem(subSystem2).subscribe((r) => {
      // console.log(r);
      this.listEquipType = [];
      const res: any = r;
      const resData = res.Body.getEquipTypeBySubSytemResponse.commonTypeConfigList;
      resData.forEach((item) => {
        this.listEquipType.push({ label: item.value, value: item.value });
      });
    });
  }
  getEquipIdByExpway() {
    this.listEquipId = [];
    const expWay = this.validateForm.value.expWay;
    const subSystem = this.validateForm.value.subSystem;
    const equipType = this.validateForm.value.equipType;
    this.reportsService.getAllEquipConfig().subscribe((resData) => {
      resData.forEach((item) => {
        // tslint:disable-next-line: max-line-length
        if (item.expwayCode && subSystem && equipType && item.expwayCode === expWay && item.subSystemId === subSystem && item.equipType === equipType) {
          this.listEquipId.push({ value: item.equipId, ischecked: false });
        }
        // item.expwayCode && item.expwayCode == expWay ? this.listEquipId.push(item.equipId) : this.listEquipId = ;
      });
      this.listEquipId = this.listEquipId.sort((a, b) => a.value.localeCompare(b.value));
      // !subSystem && !equipType || subSystem && !equipType ?
      //   this.message.create('warning', `Please select subsystem and equiptype first`) :
      //   this.listEquipId.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `--No Data--`);
      this.reportDisable = this.listEquipId.length ? false : true; // report button status     
    });
  }

  equipIdactive(i, e) {
    if (e.ctrlKey) {
      i.ischecked = i.ischecked ? false : true;
    } else {
      this.listEquipId.forEach(item => {
        item.ischecked = false;
        if (item === i) {
          item.ischecked = true;
        }
      });
    }
    this.listEquipId.forEach(item => {
      if (item.ischecked) {
        this.activeLi.activeEquiId = i;
      }
    });
    // this.activeLi.activeEquiId = i;
    // i.ischecked = i.ischecked ? false : true;
    // this.equipIdList.push(i.value);
  }

  laneTypeActive(i, e) {

    if (e.ctrlKey) {
      i.ischecked = i.ischecked ? false : true;
    } else {
      this.listLaneType.forEach(item => {
        item.ischecked = false;
        if (item === i) {
          item.ischecked = true;
        }
      });
    }
    this.listLaneType.forEach(item => {
      if (item.ischecked) {
        this.activeLi.activeLaneType = i.value;
      }
    });
    // this.activeLi.activeLaneType = i.value;
    // i.ischecked = i.ischecked ? false : true;
    // this.laneTypeList.push(i.value);
  }

  measureTypeActive(i) {
    this.activeLi.activeMeasureType = i.value;
    i.ischecked = i.ischecked ? false : true;
  }

  showModalMiddle(): void {
    const startDate = this.validateForm.value.fromDate;
    const endDate = this.validateForm.value.toDate;
    const interval = this.validateForm.value.interval;
    const expWay = this.validateForm.value.expWay;
    this.laneTypeList = [];
    this.laneTypeDescription = [];
    // console.log(expWay);
    this.listExpWay.forEach((item) => { // expway
      if (item.value == expWay) {
        // console.log(item);
        this.expWayDescription = item.label;
      }
    });
    this.listInterval.forEach((item) => { // interval
      if (item.value == interval) {
        this.intervalDescription = item.label;
      }
    });
    this.listLaneType.forEach(item => { // 获取选中的laneType
      if (item.ischecked) {
        this.laneTypeList.push(item.value);
      }
    });
    this.listLaneType.forEach((item) => { // lanetype
      Array.from(new Set(this.laneTypeList)).forEach((item1) => {
        if (item.value == item1) {
          // console.log('------------');
          this.laneTypeDescription.push(item.label);
        }
      });
    });
    this.listEquipId.forEach(item => { // 获取选中的equipId
      if (item.ischecked) {
        this.equipIdList.push(item.value);
      }
    });
    // console.log(this.laneTypeDescription);
    this.reportValue = {
      startDate: startDate,
      endDate: endDate,
      interval: this.intervalDescription,
      laneTypeList: this.laneTypeDescription,
      equipID: Array.from(new Set(this.equipIdList)),
      expWay: this.expWayDescription
    };
    const data = {
      equipIdList: Array.from(new Set(this.equipIdList)),
      laneTypeList: Array.from(new Set(this.laneTypeList)),
      dataType: this.validateForm.value.interval,
      fromDate: moment(startDate).format(),
      toDate: moment(endDate).format(),
      laneId: this.activeLi.activeMeasureType,
      subSystem: this.validateForm.value.subSystem,
      equipType: this.validateForm.value.equipType,
      expressWay: this.validateForm.value.expWay,
      withChart: this.validateForm.value.withChart
    };
    this.param = data;
    // console.log(data);
    // this.isVisibleMiddle = true;
    this.reportsService.getCountOfHistTrafficMeasure(data).subscribe((r) => {
      this.equipIdList = [];
      this.laneTypeList = [];
      // console.log(r);
      const res: any = r;
      const count = res.Body.getCountOfHistTrafficMeasureResponse.count;
      if (count < 1024) {
        this.isVisibleMiddle = true;
      } else {
        this.message.create('warning', `please change your search criteria`);
      }
      // const resData = res.
    });
  }

  closeModal() {
    this.isVisibleMiddle = false;
  }

}
