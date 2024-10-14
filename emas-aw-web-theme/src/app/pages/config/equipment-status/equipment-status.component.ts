import { Component, OnInit, OnDestroy } from '@angular/core';
import { takeUntil } from 'rxjs/operators';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { EventsService } from '../../events/events.service';
import { CommonService, EquipType } from '../../../service/common.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { UserService } from '../../user-management/user-management.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'emas-equipment-status',
  templateUrl: './equipment-status.component.html',
  styleUrls: ['./equipment-status.component.css']
})
export class EquipmentStatusComponent implements OnInit, OnDestroy {
  resResult: any;
	isVisibleMiddle = false;
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
			value: 'alarmTime',
			isChecked: true
		},
		{
			name: 'Alarm Desc',
			key: null,
			value: 'alarmDesc',
			isChecked: true
		},
		{
			name: 'Ack Time',
			key: null,
			value: 'ackTime',
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

	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
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
		private userService: UserService
  ) { }

  pageChange(e) {
		this.userService.$addData.next(e);
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
		});
	}
	async initData() {

		this.eventsService.getAllFelsCode().subscribe((res: any) => {
			this.felsCodeList = res.Body.getAllFelsCodeResponse.felsCodeList.sort(function (a, b) {
				return b.description.localeCompare(a.description);
			});
			this.validateForm.get('felsCode').setValue(this.felsCodeList[0].felsCode);
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
		const { subSystemId, location, felsCode } = this.validateForm.value;
		if (subSystemId !== 'group1') {
			const list: any = await this.commonService.filterEquipType(location, subSystemId);
			this.equipTypeList = list.map(item => {
				return { label: item, value: item };
			});
		} else {
			this.equipTypeList = [];
			this.allEquipType4Fels.forEach(res => {
				if (res.felsCode === felsCode) {
					this.equipTypeList.push({ label: res.equipmentCode, value: res.equipmentCode });
				}
			});
		}
		if (this.equipTypeList.length > 0) {
			this.equipTypeList.unshift({ label: 'All', value: 'All' });
			this.validateForm.get('equipType').setValue(this.equipTypeList[0].value);
		}
	}

	submitForm(): void {
		this.getAllData();
	}

	getAllData() {
		const { subSystemId, location, expWay, equipType, felsCode } = this.validateForm.value;
		const queryData = {
			propertyCode: [0]
		};
		if (location === 1) {
			queryData['propertyCode'] = [1, 2];
		}
		if (subSystemId !== 'All') {
			queryData['subsystemId'] = subSystemId;
			if (subSystemId === 'group1') {
				queryData['expwayCode'] = felsCode;
			}
		}

		if (expWay !== 'All') {
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
		const ttt: number = this.displayListOfData.length / 10;
		const yu = this.displayListOfData.length % 10;
		if (yu < 5) {
			this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
		} else {
			this.userService.$totalPage.next(ttt.toFixed(0));
		}
	}
	changeLocation(item): void {
		if (item !== 0) {
			this.validateForm.controls['expWay'].disable();
		} else {
			this.validateForm.controls['expWay'].enable();
		}
		this.setEquipType();
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
			// this.displayListOfData = [...arr];
      this.displayListOfData = Array.from(arr);
		}
	}
}
