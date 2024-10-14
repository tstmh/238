import { takeUntil } from 'rxjs/operators';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { EventsService } from '../events.service';
import { CommonService, EquipType } from '../../../service/common.service';
import * as moment from 'moment';
import { NzMessageService } from 'ng-zorro-antd/message';
import { UserService } from '../../user-management/user-management.service';
import { EquipmentService } from '../../equipment-control/equipment-control.service';
import { Subject } from 'rxjs';

@Component({
	selector: 'sj-technical-alarm',
	templateUrl: './technical-alarm.component.html',
	styleUrls: ['./technical-alarm.component.css']
})
export class TechnicalAlarmComponent implements OnInit, OnDestroy {
	resResult: any;
	isVisibleMiddle = false;
	buttonStatus: any = 'No'; // 底部按钮状态禁用
	choiceItemID: any;
	choiceItem: any;
	renderHeader = [
		{
			name: 'Equip ID',
			key: null,
			value: 'equipId',
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
			name: 'Alarm Time',
			key: null,
			value: 'startDate',
			isChecked: true
		},
		{
			name: 'Alarm Desc',
			key: null,
			value: 'alarmDescription',
			isChecked: true
		},
		{
			name: 'Ack Time',
			key: null,
			value: 'ackDate',
			isChecked: true
		},
		{
			name: 'Status',
			key: null,
			value: 'status',
			isChecked: true
		}
	];

	allEquipConfigList = [];
	allChecked = false;
	displayListOfData = [];
	indeterminate = true;
	subSystem = '';
	isTunnel = false;
	isLoading = false;
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	isAdmin = false;
	expwayCodeList = [
		{ label: 'All', value: 'All' }
	];
	expwayDirection = [];
	equipTypeList = [
		{ label: 'All', value: 'All' }
	];
	subSystemList = [
		{ label: 'All', value: 'All' }
	];
	listLocation = [
		{ label: 'Site', value: 0 },
		{ label: 'Backend', value: 1 }
	];
	cteCodeList = [];
	felsCodeList = [];
	allEquipType4Fels = [];
	sortName: string | null = null;
	sortValue: string | null = null;
	unSubscribe = new Subject();
	constructor(
		private fb: FormBuilder,
		private eventsService: EventsService,
		private commonService: CommonService,
		private message: NzMessageService,
		private equipmentService: EquipmentService,
		private userService: UserService
	) {
	}
	pageChange(e) {
		// console.log(e);
		this.userService.$addData.next(e)

	}
	ngOnInit() {

		this.initForm();
		this.initData();
	}

	ngOnDestroy() {
		this.unSubscribe.next();
		this.unSubscribe.complete();
	}
	initForm() {
		this.validateForm = this.fb.group({
			subSystemId: [{ value: this.subSystemList[0].value, disabled: false }],
			location: [{ value: this.listLocation[0].value, disabled: false }],
			expWay: [{ value: this.expwayCodeList[0].label, disabled: false }],
			equipType: [{ value: this.equipTypeList[0].value, disabled: false }],
			felsCode: [{ value: null, disabled: false }],
			cteCode: [{ value: null, disabled: false }]
		});
		let role = localStorage.getItem('role_id');
		this.isAdmin = (role == "ROLE_ADMIN") ? true : false;
	}
	async initData() {
		this.alarmTime();
		this.eventsService.getAllFelsCode().subscribe((res: any) => {
			this.felsCodeList = res.Body.getAllFelsCodeResponse.felsCodeList.sort(function (a, b) {
				return b.description.localeCompare(a.description);
			});
			this.validateForm.get('felsCode').setValue(this.felsCodeList[0].felsCode);
		});
		const queryData = {subsystemId: 'newctetun'};
		this.eventsService.getFelsCodeBySubsystemId(queryData).subscribe(item => {
			this.cteCodeList = item.data.sort(function (a, b) {
				return b.description.localeCompare(a.description);
			});
			this.cteCodeList.unshift({ felsCode: 'all', description: 'All' });
			this.validateForm.get('cteCode').setValue(this.cteCodeList[0].felsCode);
		});
		this.eventsService.getAllEquipType().subscribe((res: any) => {
			this.allEquipType4Fels = res.Body.getAllEquipTypeResponse.equipTypeList;
			// this.validateForm.get('felsCode').setValue(this.felsCodeList[0].felsCode);
		});

		const allType = await this.commonService.allType;
		allType[EquipType.EXPWAY_CODE].sort(function (a, b) {
			return a.description.localeCompare(b.description);
		}).forEach((item) => {
			this.expwayCodeList.push({ label: item.description, value: item.value });
		});

		this.expwayDirection = allType[EquipType.EXPWAY_DIRECTION];
		this.setEquipType();
		allType[EquipType.EMAS_SUBSYSTEM].forEach((item) => {
			this.subSystemList.push({ label: item.value, value: item.value });
		});
		this.commonService.getEquipConfig$().pipe(takeUntil(this.unSubscribe)).subscribe((tempData) => {
			this.allEquipConfigList = tempData;

			this.getAllData();
		});
	}
	async setEquipType() {
		const { subSystemId, location, felsCode, cteCode } = this.validateForm.value;
		let noNewCte = true;
		if (subSystemId !== 'group1' && subSystemId !== 'newctetun') {
			const list: any = await this.commonService.filterEquipType(location, subSystemId);
			this.equipTypeList = list.map(item => {
				return { label: item, value: item };
			});
		} else if (subSystemId === 'newctetun') {
			this.equipTypeList = [];			
			this.equipmentService.getWMSAllEquipType().subscribe(item => {
				for (let element of item.data.equipTypeVOList) {
					if (element.felsCode == cteCode) {
						this.equipTypeList.push({ label: element.type, value: element.type});
					}					
				}
			});
			noNewCte = false;
			this.equipTypeList.unshift({ label: 'All', value: 'All' });
			this.validateForm.get('equipType').setValue(this.equipTypeList[0].value);
		} else {
			this.equipTypeList = [];
			this.allEquipType4Fels.forEach(res => {
				if (res.felsCode === felsCode) {
					this.equipTypeList.push({ label: res.equipmentCode, value: res.equipmentCode });
				}
			});
		}
		if (this.equipTypeList.length > 0 && noNewCte) {
			this.equipTypeList.unshift({ label: 'All', value: 'All' });
			this.validateForm.get('equipType').setValue(this.equipTypeList[0].value);
		}
	}

	submitForm(): void {
		this.getAllData();
	}

	getAllData() {
		this.isLoading = true;
		const { subSystemId, location, expWay, equipType, felsCode, cteCode } = this.validateForm.value;
		this.subSystem = subSystemId;
		const queryData = {
			propertyCode: [0]
		};
		if (location === 1) {
			queryData['propertyCode'] = [1, 2];
		}
		if (subSystemId === 'group1') {
			this.isTunnel = true;
		} else {
			this.isTunnel = false;
		}
		if (subSystemId !== 'All') {
			queryData['subsystemId'] = subSystemId;
			if (subSystemId === 'group1') {
				queryData['expwayCode'] = felsCode;
			}
			if (subSystemId === 'newctetun' && cteCode !== 'all') {
				queryData['expwayCode'] = cteCode;
			}
		}

		if (expWay !== 'All' && location !== 1) {
			queryData['expwayCode'] = expWay;
		}
		if (equipType !== 'All') {
			queryData['equipType'] = equipType;
		}
		
		this.eventsService.GetTechnicalAlarmByEquipTypeAndExpwayCode(queryData).subscribe((res: any) => {
			const tempData: any = res.Body.getTechnicalAlarmByEquipTypeAndExpwayCodeResponse.technicalAlarmList;

			this.mergeData(tempData);
		});
	}


	mergeData(displayList = []): void {

		this.displayListOfData = displayList.map((display) => {
			for (const equipConfig of this.allEquipConfigList) {
				if (display.equipId === equipConfig.equipId) {
					display = { ...display, ...equipConfig };
					break;
				}
			}
			const tempDir = display.expwayCode + '-' + display.dir;
			display.checked = false;
			for (const expway of this.expwayCodeList) {
				if (display.expwayCode === expway.value) {
					display.expWay = expway.label;
					break;
				}
			}
			for (const direction of this.expwayDirection) {
				if (tempDir === direction.value) {
					display.direction = direction.description;
					break;
				}
			}
			display.kmMarking = parseFloat(display.kmMarking).toFixed(2);
			return display;
		});
		this.sortList();
		const ttt: number = this.displayListOfData.length / 12;
		const yu = this.displayListOfData.length % 12;
		if (yu < 5) {
			this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
		} else {
			this.userService.$totalPage.next(ttt.toFixed(0));
		}
		this.isLoading = false;
	}
	changeLocation(item): void {
		if (item !== 0) {
			this.validateForm.controls['expWay'].disable();
		} else {
			this.validateForm.controls['expWay'].enable();
		}
		this.setEquipType();
	}
	ackTechnicalAlarm(): void {

	}

	updateAllChecked(): void {
		if (this.subSystem == 'group1') {
			this.buttonStatus = 'NO';
		} else {
			this.buttonStatus = null; // 按钮状态可操作
		}
		this.indeterminate = false;
		if (this.allChecked) {
			this.displayListOfData.map(item => { item.checked = true; });
		} else {
			this.displayListOfData.map(item => { item.checked = false; });
		}
	}

	updateSingleChecked(data): void {
		if (this.subSystem == 'group1') {
			this.buttonStatus = 'NO';
		} else {
			this.buttonStatus = null; // 按钮状态可操作
		}
		this.choiceItemID = data;
		if (!data) {
			this.buttonStatus = 'NO';
		}
		if (this.displayListOfData.every(item => item.checked === false)) {
			this.allChecked = false;
			this.indeterminate = false;
		} else if (this.displayListOfData.every(item => item.checked === true)) {
			this.allChecked = true;
			this.indeterminate = false;
		} else {
			this.indeterminate = true;
		}
		// console.log(data)
	}
	createNotification(): void {
		if (this.resResult === '0') {
			this.message.create('success', `Successful operation`);
		} else {
			this.message.create('error', `The operation failed, please try again`);
		}
	}
	choice(item) {
		this.choiceItemID = item.alarmId;
		this.choiceItem = item;
	}
	buttonAck() {
		let dtoListArry = [];
		const ackBy = localStorage.getItem('user_name');
		const ackTime = moment().format();
		// console.log('ackkkkkkkkkkkkkk');
		this.displayListOfData.forEach((item) => {
			if (item.checked) { // 获取选中行数的alertId, 组成数组作为接口参数
				dtoListArry.push({ ackBy: ackBy, ackDate: ackTime, alarmId: item.alarmId });
			}
		});
		if (dtoListArry.length) {
			this.eventsService.AW_CFELS_TechAlarmAck(dtoListArry).subscribe((r) => {
				// console.log(r);
				this.getAllData();
				const res: any = r;
				const resData = res.Body.AW_CFELS_TechAlarmAckResponse;
				this.resResult = resData.result;
				this.buttonStatus = 'No';
				// console.log('---ack response---');
				// console.log(this.resResult);
				this.createNotification(); // 点击按钮后的提示框
				this.choiceItemID = null; // 点击按钮操作之后选中条目背景恢复原始样式
				this.allChecked = false;
			});
		}
	}
	buttonClear() {		
		// this.displayListOfData.forEach((item) => {
		// 	if (item.checked) {
		// 		if (item.status === '1' || item.status === '2') {
		// 			this.message.create('error', `You can only clear an acknowledged alarm`)
		// 			// MessageBox.Show("You can only clear an acknowledged alarm");
		// 			return;
		// 		} else {
		// 			this.isVisibleMiddle = true;
		// 		}
		// 	}
		// });
		if (this.displayListOfData.some(item => item.checked && (item.status === '1' || item.status === '2'))) {
			this.message.create('error', `You can only clear an acknowledged alarm`);
		} else {
			this.isVisibleMiddle = true;
		}
		
	}
	handleOkMiddle(): void {
		// console.log('click ok');
		this.isVisibleMiddle = false;
		const dtoListArry = [];
		const user = localStorage.getItem('user_name');
		const currentTime = moment().format();
		this.displayListOfData.forEach((item) => {
			if (item.checked) {
				const { ackDate, alarmId, equipId, alarmCode, equipType, ackBy, alarmDescription, systemId } = item;

				dtoListArry.push({
					startDate: currentTime, ackDate: ackDate ? ackDate : currentTime,
					ackBy, status: 2, alarmId, ackDateSpecified: true, clearBy: user, systemId,
					equipId, alarmCode, alarmDescription, startDateSpecified: true, equipType
				});
			}
		});
		if (dtoListArry.length) {
			this.eventsService.AW_CFELS_TechAlarmClear(dtoListArry, user).subscribe((r) => {
				// console.log(r);
				const res: any = r;
				this.getAllData();
				const resData = res.Body.AW_CFELS_TechAlarmClearResponse;
				this.resResult = resData.result;
				this.buttonStatus = 'No';
				this.createNotification(); // 点击按钮后的提示框
				this.allChecked = false;
				// this.choiceItemID = null; // 点击按钮操作之后选中条目背景恢复原始样式
			});
		}
	}
	handleCancelMiddle(): void {
		this.isVisibleMiddle = false;
	}
	// sort

	sort({ key, value }): void {
		this.sortName = key;
		this.sortValue = value;
		this.sortList();
	}
	sortList() {
		if (this.sortName && this.sortValue) {
			const arr = this.displayListOfData.sort((a, b) =>
				this.sortValue === 'ascend'
					? a[this.sortName] > b[this.sortName]
						? 1
						: -1
					: b[this.sortName] > a[this.sortName]
						? 1
						: -1
			);
			this.displayListOfData = [...arr];
		}
	}

	alarmTime() {	
		const nowPath = location.href;
		let pathList = nowPath.split("/");
		let tempPath = pathList[6];	
		if (tempPath != 'technical-alarm') {
			return;
		}
		setTimeout(() =>{
			const alarmUpdate = localStorage.getItem('alarmUpdate');
			if (alarmUpdate == '1') {
				this.getAllData();
				localStorage.removeItem('alarmUpdate');
			}
			this.alarmTime();
		},1000);
	}
}
