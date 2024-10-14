import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CommonService } from '../../../service/common.service';
import { EquipmentService } from '../equipment-control.service';
import { EventsService } from '../../events/events.service';
import { NzMessageService, en_US } from 'ng-zorro-antd';

@Component({
	// tslint:disable-next-line:component-selector
	selector: 'emas-fire-alarm-panel',
	templateUrl: './fire-alarm-panel.component.html',
	styleUrls: ['./fire-alarm-panel.component.css']
})
export class FireAlarmPanelComponent implements OnInit {
	renderHeader = [
		{
			name: 'Equip ID And Location',
			key: null,
			value: 'equipIdLocation',
			isChecked: true
		},
		{
			name: 'Status',
			key: null,
			value: 'status',
			isChecked: true
		},
		{
			name: 'Time',
			key: null,
			value: 'time',
			isChecked: true
		}
	];

	selectItem = {};
	equipSelect = '';
	equipTypeList = [];
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	listOfData = [];
	sortName: string | null = null;
	sortValue: string | null = null;
	listOfDisplayData = [...this.listOfData];
	displayListOfData = [];
	constructor(
		private fb: FormBuilder,
		private equipmentService: EquipmentService,
		private eventsService: EventsService,
		private commonService: CommonService,
		private message: NzMessageService,
	) { }

	ngOnInit() {
		this.initForm();
		this.getAllEquipType();
		this.getFireAlarm();
		// this.getData();
	}

	//加载Technical Alarm
	getFireAlarm(){		
		const subSystemId = 'group1';
		const equipType = 'fir';
		const expwayCode = 'af';
		const queryData = {
			propertyCode: [0]
		};		
		queryData['subsystemId'] = subSystemId;
		queryData['expwayCode'] = expwayCode;		
		queryData['equipType'] = equipType;

		this.eventsService.GetTechnicalAlarmByEquipTypeAndExpwayCode(queryData).subscribe((res: any) => {
			const tempData: any = res.Body.getTechnicalAlarmByEquipTypeAndExpwayCodeResponse.technicalAlarmList;
			this.mergeData(tempData);
		});		
	}
	mergeData(displayList = []): void {
		for (const display of displayList) {
			this.displayListOfData.push(display['equipId']);
		}
		//console.log('Data: ',this.displayListOfData);
	}

	// 请求下拉框数据
	getAllEquipType() {
		this.equipmentService.getAllEquipType().subscribe((r) => {
			const tempData: any = r;
			// this.equipTypeList = tempData.Body.getAllEquipTypeResponse.equipTypeList;
			const arr = tempData.Body.getAllEquipTypeResponse.equipTypeList;
			for (const item of arr) {
				if (item.description.indexOf('Sub Alarm Panel') !== -1) {
					this.equipTypeList.push(item);
				} else if (item.description.indexOf('Main Fire Alarm Panel') !== -1) {
					this.equipTypeList.push(item);
				} else if (item.description.indexOf('Flammable Gas Sensor Monitoring') !== -1) {
					this.equipTypeList.push(item);
				} else if (item.description.indexOf('Fire Alarm System Class') !== -1) {
					this.equipTypeList.push(item);
				}
			}
			this.equipSelect = this.equipTypeList[0].description;
		});
	}
	// equipType下拉框内容改变
	equipSearch() {
		// 下拉框改变请求数据
		this.equipTypeList.forEach(item => {
			if (item.description === this.equipSelect) {
				// console.log(item.equipmentCode, item.felsCode);
				const queryData = {
					felsCode: item.felsCode,
					equipType: item.equipmentCode,
					attrName: 'ope'
				};
				this.equipmentService.getEquipStatusByEquipType(queryData).subscribe((r) => {
					const tempData: any = r;
					if (tempData.Body.getEquipStatusByEquipTypeResponse.equipStatusList) {
						this.listOfData = tempData.Body.getEquipStatusByEquipTypeResponse.equipStatusList.map(e => {
							const param = { 'equipId': e.equipmentId, 'equipCode': e.equipmentCode, 'location': e.location, 'felsCode': e.felsCode };
							e['idAndLoc'] = this.equipmentService.covert(param);
							return e;
						});
					} else {
						this.message.create('error', `No Data`);
						this.listOfData = [];
					}
					this.listOfDisplayData = [...this.listOfData];
				});
			}
		});
		// this.equipTypeList.forEach(item => {
		// 	if (item.description === this.equipSelect) {
		// 		// console.log(item.equipmentCode, item.felsCode);
		// 		const queryData = {
		// 			felsCode: item.felsCode,
		// 			equipType: item.equipmentCode,
		// 		};
		// 		this.equipmentService.getTechnicalAlarmByEquipType(queryData).subscribe((r) => {
		// 			const tempData: any = r;
		// 			console.log(tempData.Body.getTechnicalAlarmByEquipTypeResponse.techAlarmList);
		// 			if (tempData.Body.getTechnicalAlarmByEquipTypeResponse.techAlarmList) {
		// 				this.listOfData = tempData.Body.getTechnicalAlarmByEquipTypeResponse.techAlarmList;
		// 			} else {
		// 				this.message.create('error', `No Data`);
		// 				this.listOfData = [];
		// 			}
		// 		});
		// 	}
		// });
	}
	initForm() {
		this.validateForm = this.fb.group({
			equipType: [{ value: null, disabled: false }],
		});
	}
	submitForm(): void {
	}

	// sort data
	sort(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
	}
	search(): void {
		const data = this.listOfData;
		if (this.sortName && this.sortValue) {
			this.listOfDisplayData = [];
			const arr = data.sort((a, b) =>
				this.sortValue === 'ascend'
					// tslint:disable-next-line:no-non-null-assertion
					? a[this.sortName!] > b[this.sortName!]
						? 1
						: -1
					// tslint:disable-next-line:no-non-null-assertion
					: b[this.sortName!] > a[this.sortName!]
						? 1
						: -1
			);
			this.listOfDisplayData = [...arr];
		} else {
			this.listOfDisplayData = [...data];
		}
	}
	clickTr(data): void {
		this.selectItem = data;
	}

}
