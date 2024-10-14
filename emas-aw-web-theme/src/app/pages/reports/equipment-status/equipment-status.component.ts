import { EquipType } from './../../../service/common.service';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { CommonService } from 'src/app/service/common.service';
import { ReportsService } from '../reports.service';
import { isTemplateRef, NzMessageService } from 'ng-zorro-antd';
import * as moment from 'moment';

@Component({
  selector: 'emas-equipment-status',
  templateUrl: './equipment-status.component.html',
  styleUrls: ['./equipment-status.component.css']
})
export class EquipmentStatusComponent implements OnInit {
  withChart = null;
  isSpinning = false;
  isVisibleMiddle = false;
  validateForm: FormGroup;
  listSubSystem = [{ label: 'All', value: 'All' }];
  listLocation = [
    { label: 'Site', value: 0 },
    { label: 'Backend', value: 1 }
  ];
  listExpWay = [];
  listEquipType = [];
  listEquipID = [];
  Generate = false;
  expWayDisabled = false;
  newArr = [];
  equipIdList = [];
  listData = [];
  reportValue = {
    startDate: null,
    endDate: null,
    equipType: null,
    equipID: null,
    expWay: null,
    expWayDescription: null
  };
  constructor(
    private fb: FormBuilder,
    private commonService: CommonService,
    private reportsService: ReportsService,
    private message: NzMessageService
  ) { }

  ngOnInit() {
    this.initForm();
    this.getData();
  }
  initForm() {

    this.validateForm = this.fb.group({
      subSystem: [{ value: 'All', disabled: false }],
      equipID: [{ value: null, disabled: false }],
      location: [{ value: 0, disabled: false }],
      expWay: [{ value: null, disabled: false }],
      equipType: [{ value: 'All', disabled: false }],
      fromDate: [{ value: new Date(), disabled: false }],
      toDate: [{ value: new Date(), disabled: false }],
      withChart: [{ vale: null, disabled: false }]
    });
  }
  submitForm(): void {
    // console.log(this.validateForm);
  }

  async getData() {

    // 获取subSystem下拉框数据

    const allType = await this.commonService.allType;

    // this.listSubSystem = [];
    allType[EquipType.EMAS_SUBSYSTEM].forEach((item) => {
      this.listSubSystem.push({ label: item.value, value: item.value });
    });
    // this.validateForm.get('subSystem').setValue(this.listSubSystem[0].label);

    // 获取expway下拉框数据
    allType[EquipType.EXPWAY_CODE].sort((a, b) => a.description.localeCompare(b.description))
      .forEach((item) => {
        this.listExpWay.push({ label: item.description, value: item.value });
      });
    this.validateForm.get('expWay').setValue(this.listExpWay[0].value);
    this.changeEquip();
  }

  changeEquip(changeEquipType?: boolean) {
    const subSystemValue = this.validateForm.value.subSystem;
    const location = this.validateForm.value.location;
    const expWay = this.validateForm.value.expWay;

    if (!changeEquipType) {
      this.validateForm.get('equipType').setValue('All');
    }
    const equipType = this.validateForm.value.equipType;
    this.reportsService.getAllEquipConfig().subscribe(newData => {
      this.listEquipID = [];
      const equipTypeSet = new Set();
      newData.forEach(item => {
        if (location === 1) { // 'Backend'
          if ((item.propertyCode == 1 || item.propertyCode == 2)
            && (item.subSystemId == subSystemValue || subSystemValue === 'All')) {
            equipTypeSet.add(item.equipType);
            if (equipType === 'All' || equipType === item.equipType) {
              this.listEquipID.push(item);
            }
          }
        } else if (item.propertyCode == 0
          && (item.subSystemId == subSystemValue || subSystemValue === 'All')) {
          equipTypeSet.add(item.equipType);
          if ((equipType === 'All' || equipType === item.equipType)
            && item.expwayCode == expWay) {
            this.listEquipID.push(item);
          }
        }
      });
      this.listEquipType = [...equipTypeSet].sort().map(e => { return { label: e, value: e } });
      if (this.listEquipID.length > 0) { // 默认选中 all
        this.listEquipID.unshift({ 'equipId': 'All', 'ischecked': true });
        this.equipIdList = ['All'];
      }
      if (this.listEquipType.length > 0) {
        this.listEquipType.unshift({ 'label': 'All', value: 'All' });
      }
    });

  }

  clickTr(i, e) {
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
        this.equipIdList.push(item.equipId);
      }
    });
  }
  GenerateClick() {
    const { equipType, expWay, withChart, subSystem, location } = this.validateForm.value;
    const startDate = this.validateForm.value.fromDate;
    const endDate = this.validateForm.value.toDate;
    const equipID = this.equipIdList;
    let expWayDescription = '';
    const propCodeList = location ? [1, 2] : [0];
    this.withChart = withChart;
    this.listExpWay.forEach(item => {
      if (item.value === expWay) {
        expWayDescription = item.label;
      }
    });
    this.reportValue = {
      startDate,
      endDate,
      equipType,
      equipID,
      expWay,
      expWayDescription
    };
    if (equipID.length > 0) {
      const data = {
        fromDate: moment(startDate).format(),
        toDate: moment(endDate).format(),
        propCodeList,
        // subSystem,
        // equipType,
        // equipIdList: this.equipIdList,
        // expwayCode: expWay
      };
      if (equipID.indexOf('All') < 0) {
        data['equipIdList'] = Array.from(new Set(equipID));
      }
      if (equipType !== 'All') {
        data['equipType'] = equipType;
      }
      if (subSystem !== 'All') {
        data['subSystem'] = subSystem;
      }
      if (location !== 1) {
        data['expwayCode'] = expWay;
      }
      this.getListData(data);
    } else {
      this.message.create('warning', 'Please select an item');
    }
  }
  getListData(data1) {
    this.isSpinning = true;
    this.reportsService.getCountOfHistEquipStatus(data1).subscribe(r => {
      const tempData = r;
      if (tempData['Body'].getCountOfHistEquipStatusResponse.count < 10000) {
        this.reportsService.getHistEquipStatusByEquipIdAndDate(data1).subscribe(res => {
          // console.log(res);
          const resData = res['Body'].getHistEquipStatusByEquipIdAndDateResponse.histEquipStatusList;
          const middleData = resData ? resData : [];
          if (middleData) {
            setTimeout(() => {
              this.isSpinning = false;
              this.isVisibleMiddle = true;
            }, 2000);
          } else {
            this.isSpinning = false;
            this.isVisibleMiddle = true;
            this.message.create('warning', `No records Found`);
          }
          this.listData = middleData;
        });
      } else {
        this.message.create('warning', `please change your search criteria`);
        this.isSpinning = false;
      }
    });
  }
  closeModal() {
    // this.equipIdList = [];
    this.isVisibleMiddle = false;
    this.listEquipID.forEach(item => {
      // tslint:disable-next-line:no-unused-expression
      item.ischecked === item.ischecked ? false : true;
    });
  }
  fromDateChange(result: Date): void {
    // console.log('Selected Time: ', result);
  }
  toDateChange(result: Date): void {
    // console.log('Selected Time: ', result);
  }

}
