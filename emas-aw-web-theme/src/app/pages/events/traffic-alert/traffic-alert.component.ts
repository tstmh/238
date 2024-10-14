import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { EventsService } from '../events.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { CommonService, EquipType } from '../../../service/common.service';
import * as moment from 'moment';
import { UserService } from '../../user-management/user-management.service';
import { takeUntil } from 'rxjs/operators';

//scroll loading
export interface Alert {
	checked: boolean;
	alertCode: string;
	alertDate: Date;
	alertId: string;
	alertType: string;
	equipConfigDto: any;
	equipId: string;
	imageFileName: string;
	imageUrl: string;
	laneId: string;
	laneType: string;
	videoUrl: string;
}

@Component({
	// tslint:disable-next-line: component-selector
	selector: 'sj-traffic-alert',
	templateUrl: './traffic-alert.component.html',
	styleUrls: ['./traffic-alert.component.css']
})
export class TrafficAlertComponent implements OnInit, AfterViewInit {
	allType = {};
	renderHeader = [
		{
			name: 'Equip ID',
			key: null,
			value: 'equipId',
			isChecked: true
		},
		{
			name: 'Alert Time',
			key: null,
			value: 'alertTime',
			isChecked: true
		},
		{
			name: 'Exp Way',
			key: null,
			value: 'expWay',
			isChecked: true
		},
		{
			name: 'Km Marking',
			key: null,
			value: 'kmMarking',
			isChecked: true
		},
		{
			name: 'Direction',
			key: null,
			value: 'direction',
			isChecked: true
		},
		{
			name: 'Lane Type',
			key: null,
			value: 'laneType',
			isChecked: true
		},
		{
			name: 'Lane No',
			key: null,
			value: 'laneNo',
			isChecked: true
		},
		{
			name: 'Alert Type',
			key: null,
			value: 'alertType',
			isChecked: true
		},
		{
			name: 'End Time',
			key: null,
			value: 'endTime',
			isChecked: true
		},
		{
			name: 'View Image',
			key: null,
			value: 'viewImage',
			isChecked: true
		},
		{
			name: 'View Video',
			key: null,
			value: 'viewVideo',
			isChecked: true
		},
		{
			name: 'Output Result',
			key: null,
			value: 'outputResult',
			isChecked: true
		},
		{
			name: 'Output Reason',
			key: null,
			value: 'outputReason',
			isChecked: true
		}
	];

	listOfData = [];
	//scroll loading
	//listOfData: Alert[] = [];
	listEquipID = [
		{ label: 'All', value: 0 },
	];
	listAlertListBy = [
		{ label: 'Equip ID', value: 1 },
		{ label: 'Alert Type', value: 2 },
		{ label: 'Time', value: 3 }
	];
	listAlertType = [];
	listExpWay = [
		{ label: 'All', value: 0 },
	];
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	isVisibleMiddle = false;
	alertListByValue = 1;
	expwayCodeList = [];
	expwayDirection = [];
	alertTypeList = [];
	choiceItem: any;
	choiceItemID: any;
	resResult: number;
	buttonStatus: any = true; // 底部按钮状态禁用
	imgUrl: any;
	videoUrl: any;
	isVideo = false;
	allChecked = false;
	indeterminate = true;
	// 时间选择器
	startValue: Date | null = null;
	endValue: Date | null = null;
	endOpen = false;
	isLoading = false;
	sortName: string | null = null;
	sortValue: string | null = null;

	constructor(
		private fb: FormBuilder,
		private eventsService: EventsService,
		private message: NzMessageService,
		private commonService: CommonService,
		private userService: UserService,
	) { }

	ngOnInit() {
		this.initForm();
		this.getAllData();
	}

	//scroll loading
	ngAfterViewInit() {
		let theTable = document.getElementsByClassName('cdk-virtual-scroll-viewport')
		if (theTable && theTable[0]) {
			theTable[0].setAttribute('style', 'overflow-x: hidden !important')
		}
	}

	initForm() {
		this.validateForm = this.fb.group({
			expWay: [{ value: null, disabled: false }],
			alertListBy: [{ value: null, disabled: false }],
			alertType: [{ value: null, disabled: false }],
			equipId: [{ value: null, disabled: false }],
			fromDate: [{ value: new Date(), disabled: false }],
			toDate: [{ value: new Date(), disabled: false }]
		});
	}
	submitForm(): void {
		this.isLoading = true;
		const expwayCode = this.validateForm.value.expWay;
		const equipId = this.validateForm.value.equipId;
		const alertType = this.validateForm.value.alertType;
		const fromDate = this.validateForm.value.fromDate;
		const toDate = this.validateForm.value.toDate;
		const transferFromDate = moment(fromDate).format();
		const transferToDate = moment(toDate).format();
		// console.log(expwayCode);
		// console.log(equipId);
		// console.log(alertType);
		// console.log(transferFromDate);
		// console.log(transferToDate);
		if (equipId && expwayCode && this.alertListByValue == 1) { // equipID
			const parameter = { equipId, expwayCode };
			this.eventsService.getTrafficAlertByEquipIdAndExpwayCode(parameter).subscribe((r) => {

				const res: any = r;
				const resData = res.Body.getTrafficAlertByEquipIdAndExpwayCodeResponse.trafficAlertList;
				const handeData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined

				this.mergeData(handeData);
				this.listOfData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
				this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			});
		} else if (alertType && this.alertListByValue == 2) { // alert type
			const parameter = { alertType, expwayCode };
			expwayCode ? this.eventsService.getTrafficAlertByAlertTypeAndExpwayCode(parameter).subscribe((r) => {

				const res: any = r;
				const resData = res.Body.getTrafficAlertByAlertTypeAndExpwayCodeResponse.trafficAlertList;
				const handeData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
				this.mergeData(handeData);
				this.listOfData = resData ? [...resData] : [];
				this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			}) : this.getDataByalertType(alertType);
		} else if (fromDate && toDate && this.alertListByValue == 3) { // time
			const parameter = { 'startDate': transferFromDate, 'endDate': transferToDate, 'expwayCode': expwayCode };
			expwayCode ? this.eventsService.getTrafficAlertByAlertTimeAndExpwayCode(parameter).subscribe((r) => {

				const res: any = r;
				const resData = res.Body.getTrafficAlertByAlertTimeAndExpwayCodeResponse.trafficAlertList;
				const handeData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
				this.mergeData(handeData);
				this.listOfData = resData ? [...resData] : [];
				this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			}) : this.getDataByTime(transferFromDate, transferToDate);
		} else if (!expwayCode && !equipId && this.alertListByValue == 1) { // expway >> all equipid >> all

			const parameter = {};
			this.eventsService.getTrafficAlertByEquipIdAndExpwayCode(parameter).subscribe((r) => {

				const res: any = r;
				const resData = res.Body.getTrafficAlertByEquipIdAndExpwayCodeResponse.trafficAlertList;

				const handeData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
				this.mergeData(handeData);
				this.listOfData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
			});
		} else if (expwayCode && !equipId && this.alertListByValue == 1) { // equipid >> all
			this.getDataByEquipId(expwayCode, 'expwayCode');
		} else if (equipId && !expwayCode && this.alertListByValue == 1) { // expway >> all
			this.getDataByEquipId(equipId, 'equipId');
		}
		const ttt: number = this.listOfData.length / 10;
		const yu = this.listOfData.length % 10;
		if (yu < 5) {
			// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
			this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
		} else {
			// localStorage.setItem('totalPage', ttt.toFixed(0));
			this.userService.$totalPage.next(ttt.toFixed(0));
		}
	}

	pageChange(e) {
		this.userService.$addData.next(e)
	}

	//scroll loading
	trackByIndex(_: number, data: Alert): string {
		//console.log('the first param is:', _)
		return data.equipId;
	}

	async getAllData() {
		this.isLoading = true;
		this.allType = await this.commonService.allType;

		const listEaxpWayCode = this.allType[EquipType.EXPWAY_CODE];
		const listAlertTypeCode = this.allType[EquipType.ALERT_TYPE];
		const parameter = {};
		listEaxpWayCode.forEach((item) => {
			this.listExpWay.push({ label: item.description, value: item.value });
		});
		listAlertTypeCode.forEach((item) => {
			this.listAlertType.push({ label: item.description, value: item.value });
		});
		this.validateForm.get('alertType').setValue(this.listAlertType[0].value);
		this.eventsService.getTrafficAlertByEquipIdAndExpwayCode(parameter).subscribe((r) => {
			const res: any = r;
			const resData = res.Body.getTrafficAlertByEquipIdAndExpwayCodeResponse.trafficAlertList;
			const handeData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
			// console.log(resData instanceof Object);
			this.mergeData(handeData);
			this.listOfData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
			this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			const ttt: number = this.listOfData.length / 10;
			const yu = this.listOfData.length % 10;
			this.listOfData.forEach((item) => {
				item.equipConfigDto.kmMarking = this.returnFloat(item.equipConfigDto.kmMarking);
			});
			if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			} else {
				// localStorage.setItem('totalPage', ttt.toFixed(0));
				this.userService.$totalPage.next(ttt.toFixed(0));
			}
			this.isLoading = false;
		});
		this.getEquipId('allEquiId');
	}

	getDataByEquipId(type, type1) {
		const parameter = {};
		const displayData = [];
		this.eventsService.getTrafficAlertByEquipIdAndExpwayCode(parameter).subscribe((r) => {

			const res: any = r;
			const resData = res.Body.getTrafficAlertByEquipIdAndExpwayCodeResponse.trafficAlertList;
			// console.log(resData instanceof Object);
			const handeData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
			this.mergeData(handeData);
			const filterData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
			if (type1 === 'equipId') {
				filterData.forEach((item) => {
					if (item.equipConfigDto.equipId == type) {
						displayData.push(item);
					}
				});
				this.listOfData = displayData;
				this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			} else {
				filterData.forEach((item) => {
					if (item.equipConfigDto.expwayCode == type) {
						displayData.push(item);
					}
				});
				this.listOfData = displayData;
				this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			}
			const ttt: number = this.listOfData.length / 10;
			const yu = this.listOfData.length % 10;
			if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			} else {
				// localStorage.setItem('totalPage', ttt.toFixed(0));
				this.userService.$totalPage.next(ttt.toFixed(0));
			}
		});
	}
	// 返回的数字保留到小数点后两位
	returnFloat(kmMarking) {
		// console.log(kmMarking)
		const value = Math.round(parseFloat(kmMarking) * 100) / 100;
		const xsd = value.toString().split('.');
		if (xsd.length === 1) {
			kmMarking = value.toString() + '.00';
			return kmMarking;
		}
		if (xsd.length > 1) {
			if (xsd[1].length < 2) {
				kmMarking = value.toString() + '0';
			}
			return kmMarking;
		}
		// console.log(kmMarking)
	}
	getDataByalertType(type) {
		const parameter = { type };
		const displayData = [];
		this.eventsService.getTrafficAlertByEquipIdAndExpwayCode(parameter).subscribe((r) => {

			const res: any = r;
			const resData = res.Body.getTrafficAlertByEquipIdAndExpwayCodeResponse.trafficAlertList;
			const handeData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
			this.mergeData(handeData);
			const filterData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
			filterData.forEach((item) => {
				if (item.alertCode == type) {
					displayData.push(item);
				}
			});
			this.listOfData = displayData;
			this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			const ttt: number = this.listOfData.length / 10;
			const yu = this.listOfData.length % 10;
			if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			} else {
				// localStorage.setItem('totalPage', ttt.toFixed(0));
				this.userService.$totalPage.next(ttt.toFixed(0));
			}
		});
	}

	getDataByTime(transferFromDate, transferToDate) {
		const parameter = { 'startDate': transferFromDate, 'endDate': transferToDate };
		this.eventsService.getTrafficAlertByAlertTimeAndExpwayCode(parameter).subscribe((r) => {

			const res: any = r;
			const resData = res.Body.getTrafficAlertByAlertTimeAndExpwayCodeResponse.trafficAlertList;
			const handeData = resData ? [...resData] : []; // 防止没有数据时后台返回undefined
			this.mergeData(handeData);
			this.listOfData = resData ? [...resData] : [];
			this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
		});
	}

	getEquipIdByexpWay() {
		this.listEquipID = [{ label: 'All', value: 0 }];
		const expwayCode = this.validateForm.value.expWay;
		expwayCode ? this.getEquipId('byExpway') : this.getEquipId('allEquiId');
	}

	getEquipId(type?) {
		const expwayCode = this.validateForm.value.expWay;
		const listEaxpWayCode = this.allType[EquipType.EXPWAY_CODE];
		this.eventsService.GetAllEquipConfig().subscribe((r) => {
			const tempData: any = r;
			const res = tempData.Body.getAllEquipConfigResponse.equipConfigDtoList;
			if (type === 'byExpway') {
				res.forEach((item) => {
					if (item.expwayCode == expwayCode && item.equipType == 'dtt') {
						this.listEquipID.push({ label: item.equipId, value: item.equipId });
					}
				});
				if (this.listEquipID.length === 1) {
					this.listEquipID = [];
				}
			}
			if (type === 'allEquiId') {
				res.forEach((item) => {
					listEaxpWayCode.forEach((item1) => {
						if (item.expwayCode == item1.value && item.equipType == 'dtt') {
							this.listEquipID.push({ label: item.equipId, value: item.equipId });
						}
					});
				});
			}
		});
	}

	buttonAck(num) {
		const dtoListArry = [];
		const ackBy = localStorage.getItem('user_name');
		const ackStatus = num;
		const ackTime = moment().format();
		this.listOfData.forEach((item) => {
			if (item.checked) { // 获取选中行数的alertId, 组成数组作为接口参数
				dtoListArry.push({ ackBy: ackBy, ackStatus: ackStatus, ackTime: ackTime, alertId: item.alertId });
			}
		});
		if (dtoListArry.length) {
			this.eventsService.AW_CFELS_TrafficAlertAck(dtoListArry).subscribe((r) => {

				const res: any = r;
				const resData = res.Body.AW_CFELS_TrafficAlertAckResponse;
				this.resResult = resData.result;
				this.buttonStatus = true;
				this.createNotification(); // 点击按钮后的提示框
				this.choiceItemID = null; // 点击按钮操作之后选中条目背景恢复原始样式
			});
		}
	}


	mergeData(sourceData): void { // 获取expway/direction/alertType
		sourceData.forEach((item) => {
			item.equipConfigDto.kmMarking = this.returnFloat(item.equipConfigDto.kmMarking);
		});
		if (sourceData) {
			const tempDir = [];
			sourceData.forEach((item, i) => {
				tempDir[i] = item.equipConfigDto.expwayCode + '-' + item.equipConfigDto.dir;
			});
			this.expwayCodeList = this.allType[EquipType.EXPWAY_CODE];
			this.expwayDirection = this.allType[EquipType.EXPWAY_DIRECTION];
			this.alertTypeList = this.allType[EquipType.ALERT_TYPE];
			sourceData.forEach((dataA) => {
				this.expwayCodeList.forEach((dataB) => {
					if (dataA.equipConfigDto.expwayCode === dataB.value) {
						dataA.equipConfigDto.expWay = dataB.description;
					}
				});
			});
			tempDir.forEach((dataA, i) => {
				this.expwayDirection.forEach((dataB) => {
					if (dataA === dataB.value) {
						sourceData[i].equipConfigDto.direction = dataB.description;
					}
				});
			});
			sourceData.forEach((dataA) => {
				this.alertTypeList.forEach((dataC) => {
					if (dataA.alertCode === dataC.value) {
						dataA.alertType = dataC.description;
					}
				});
			});
			// this.search();
			// const ttt: number = sourceData.length / 13;
			// const yu = sourceData.length % 13;
			// if (yu < 5) {
			// 	this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			// } else {
			// 	this.userService.$totalPage.next(ttt.toFixed(0));
			// }
		}
		this.isLoading = false;
	}

	ngModelChange() {
		this.alertListByValue = this.validateForm.value.alertListBy;
	}

	choice(item) {
		this.choiceItemID = item.alertId;
		this.choiceItem = item;
	}

	// 时间选择器
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
	}

	handleEndOpenChange(open: boolean): void {
		this.endOpen = open;
	}

	// 提示框
	createNotification(): void {
		if (this.resResult == 1) { // 0代表成功 1代表失败
			this.message.create('error', `The operation failed, please try again`);
		} else {
			// this.message.create('error', `The operation failed, please try again`);
			this.message.create('success', `Successful operation`);
		}
	}

	// // sort
	sort(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
	}
	search(): void {
		const data = this.listOfData;
		/** sort data **/
		if (this.sortName && this.sortValue) {
			const arr = data.sort((a, b) =>
				this.sortValue === 'ascend'
					// tslint:disable-next-line: no-non-null-assertion
					? a[this.sortName!] > b[this.sortName!]
						? 1
						: -1
					// tslint:disable-next-line: no-non-null-assertion
					: b[this.sortName!] > a[this.sortName!]
						? 1
						: -1
			);
			this.listOfData = [...arr];
		} else {
			this.listOfData = [...data];
		}
	}

	showModalMiddle(url,isV): void {
		this.isVisibleMiddle = true;	
		//if (url.substr(url.length()-3,url.length()).equals('avi'||'AVI')) {
		if (isV) {
			this.isVideo = true;
			this.videoUrl = url;
		} else {
			this.isVideo = false;
			this.imgUrl = url;
		}
	}

	closeModal() {
		this.isVisibleMiddle = false;
	}

	updateAllChecked(): void {
		this.buttonStatus = false;
		this.indeterminate = false;
		if (this.allChecked) {
			this.listOfData.map(item => { item.checked = true; });
		} else {
			this.listOfData.map(item => { item.checked = false; });
		}
	}

	updateSingleChecked(data): void {
		this.buttonStatus = false; // 按钮状态可操作
		this.choiceItemID = data;
		if (this.listOfData.every(item => item.checked === false)) {
			this.allChecked = false;
			this.indeterminate = false;
			this.buttonStatus = true;
		} else if (this.listOfData.every(item => item.checked === true)) {
			this.allChecked = true;
			this.indeterminate = false;
		} else {
			this.indeterminate = true;
		}
	}

}
