import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { EventsService } from '../events.service';
import { CommonService, EquipType } from '../../../service/common.service';

import { NzMessageService } from 'ng-zorro-antd';
@Component({
	selector: 'sj-traffic-measure',
	templateUrl: './traffic-measure.component.html',
	styleUrls: ['./traffic-measure.component.css']
})
export class TrafficMeasureComponent implements OnInit {
	showData = false;
	oneData = '';
	fiveData = '';
	hourData = '';
	sortName: string | null = null;
	sortValue: string | null = null;
	displayListOne = [];
	displayListFive = [];
	displayListSixty = [];
	renderHeader = [
		{
			name: 'Lane ID',
			key: null,
			value: 'laneId',
			isChecked: true
		},
		// {
		// 	name: 'Lane Type',
		// 	key: null,
		// 	value: 'laneType',
		// 	isChecked: true
		// },
		{
			name: 'Vol(veh/hr)',
			key: null,
			value: 'vol',
			isChecked: true
		},
		{
			name: 'Speed(Km/hr)',
			key: null,
			value: 'speed',
			isChecked: true
		},
		{
			name: 'Occ(%)',
			key: null,
			value: 'occ',
			isChecked: true
		},
		{
			name: 'Headway(m)',
			key: null,
			value: 'headWay',
			isChecked: true
		},
		{
			name: 'Avg Delay(s)',
			key: null,
			value: 'avgDelay',
			isChecked: true
		},
		{
			name: 'Avg Length(s)',
			key: null,
			value: 'avgLength',
			isChecked: true
		},
		{
			name: 'Class1(count)',
			key: null,
			value: 'calss1',
			isChecked: true
		},
		{
			name: 'Class2(count)',
			key: null,
			value: 'class2',
			isChecked: true
		},
		{
			name: 'Class3(count)',
			key: null,
			value: 'class3',
			isChecked: true
		},
		{
			name: 'Class4(count)',
			key: null,
			value: 'class4',
			isChecked: true
		},
		{
			name: 'Class5(count)',
			key: null,
			value: 'class5',
			isChecked: true
		},
		{
			name: 'Total(count)',
			key: null,
			value: 'total',
			isChecked: true
		},
	];

	listExpWay: Array<{ label: string; value: number }> = [
		{ label: 'AYE', value: 0 },
		{ label: 'ExpWay', value: 1 },
		{ label: 'ExpWay', value: 2 },
	];
	listEquipType = [
		{ label: 'All', value: 0 },
		{ label: 'equipeType', value: 1 },
		{ label: 'equipeType', value: 2 }
	];
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	listOfData = [];
	listOfDataA = [];
	listOfDataB = [];
	listOfEquid = [];
	listOfByEquid: any = [{
		equipId: '--NO Data--'
	}];
	expWapList = [];
	equipTypeList = [];
	expwayCodeList: any = [];
	expwayDirection = [];
	OneMinList = [];
	FiveMinList = [];
	SixtyMinList = [];
	buttonStatus = true;
	EquipId = '';
	constructor(
		private fb: FormBuilder,
		private eventsService: EventsService,
		private commonService: CommonService,
		private message: NzMessageService,
	) {
	}
	// sort-One

	sort(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
	}
	search(): void {
		const data = this.OneMinList;
		this.displayListOne = [];
		/** sort data **/
		if (this.sortName && this.sortValue) {
			const arr = data.sort((a, b) =>
				this.sortValue === 'ascend'
					? a[this.sortName!] > b[this.sortName!]
						? 1
						: -1
					: b[this.sortName!] > a[this.sortName!]
						? 1
						: -1
			);
			this.displayListOne = [...arr];
		} else {
			this.displayListOne = [...data];
		}
	}

	// sort-Two

	sortTwo(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.searchTwo();
	}
	searchTwo(): void {
		const data = this.FiveMinList;
		// console.log(this.displayListFive);
		// console.log(this.sortValue);
		this.displayListFive = [];
		/** sort data **/
		if (this.sortName && this.sortValue) {
			const arr = data.sort((a, b) =>
				this.sortValue === 'ascend'
					? a[this.sortName!] > b[this.sortName!]
						? 1
						: -1
					: b[this.sortName!] > a[this.sortName!]
						? 1
						: -1
			);
			this.displayListFive = [...arr];
			// console.log(this.displayListFive);
		} else {
			this.displayListFive = [...data];
			// console.log(this.displayListFive);
		}
	}
	// sort-Two

	sortThree(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.searchThree();
	}
	searchThree(): void {
		const data = this.SixtyMinList;
		// console.log(this.displayListSixty);
		// console.log(this.sortValue);
		this.displayListSixty = [];
		/** sort data **/
		if (this.sortName && this.sortValue) {
			const arr = data.sort((a, b) =>
				this.sortValue === 'ascend'
					? a[this.sortName!] > b[this.sortName!]
						? 1
						: -1
					: b[this.sortName!] > a[this.sortName!]
						? 1
						: -1
			);
			this.displayListSixty = [...arr];
		} else {
			this.displayListSixty = [...data];
		}
	}
	ngOnInit() {
		this.initForm();
		this.initData();
		setTimeout(() => {
			this.mergeData(this.listOfData, this.listOfDataA);
		}, 2000);
	}

	changeExpWay(event) {
		this.listOfEquid = [];
		this.listOfByEquid = [];
		this.listOfDataA.forEach((item) => {
			if (event === item.expwayCode) {
				if(item.equipType === 'dtt'){
					this.listOfEquid.push(item);
				}
			}
		});
		if (this.listOfEquid.length === 0) {
			//this.listOfEquid.push({equipId: '--NO Data--'});
			this.validateForm.patchValue({
				equipId: this.listOfEquid[0] ? this.listOfEquid[0].equipId : ''
			});
			this.buttonStatus = true;
		} else {
			this.validateForm.patchValue({
				equipId: this.listOfEquid[1].equipId
			});
		}
		this.listOfByEquid = this.listOfEquid;
	}
	initForm() {
		this.validateForm = this.fb.group({
			expwayCode: [{ value: null, disabled: false }],
			// expWay: [{ value: null, disabled: false }],
			// equipType: [{ value: null, disabled: false }],
			equipId: [{ value: null, disabled: false }]
		});
		this.validateForm.get('equipId').valueChanges.subscribe(value => {
			if (value) {
				this.buttonStatus = false;
			}
		});
	}
	formPatchValue(): void {
		this.listOfDataA.forEach((item) => {
			if (this.expwayCodeList[0].value === item.expwayCode) {
				this.listOfEquid.push(item);
			}
		});
		this.validateForm.patchValue({
			expwayCode: this.expwayCodeList[0].value,
			equipId: this.listOfEquid[1] ? this.listOfEquid[1].equipId : ''
		});
	}
	changeType(e) {
		this.EquipId = e;
	}
	submitForm(): void {
		// 一分钟
		const OneData = {
			'equipId': this.validateForm.value.equipId,
			'expwayCode': this.validateForm.value.expwayCode,
			'dataType': 1
		};
		this.eventsService.GetTrafficMeasureByEquipIdAndExpwayCode(OneData).subscribe((r) => {

			const tempData: any = r;

			if (!tempData.Body.getTrafficMeasureByEquipIdAndExpwayCodeResponse.trafficMeasureList) {

				this.showData = false
				this.message.create('success', `No data`);
			} else {
				this.showData = true
			}

			this.OneMinList = tempData.Body.getTrafficMeasureByEquipIdAndExpwayCodeResponse.trafficMeasureList ? tempData.Body.getTrafficMeasureByEquipIdAndExpwayCodeResponse.trafficMeasureList : []
			this.OneMinList.forEach((itemA) => {
				itemA.newParam = 'total';
				itemA.totalClass = parseInt(itemA.class1Count) + parseInt(itemA.class2Count) + parseInt(itemA.class3Count) + parseInt(itemA.class4Count) + parseInt(itemA.class5Count)
			});
			if (this.OneMinList.length > 0) {
				this.oneData = this.OneMinList[0] ? this.OneMinList[0].dateTime : ''
			}

			const tempA = [];
			const tempB = [];
			this.OneMinList.forEach(item => {
				tempA.push(item.laneId);
			});
			tempA.sort();

			tempA.forEach(landid => {
				this.OneMinList.forEach(item => {
					if (landid === item.laneId) {
						tempB.push(item);
					}
				});
			});

			this.displayListOne = tempB;
		});
		// 五分钟
		const FiveData = {
			'equipId': this.validateForm.value.equipId,
			'expwayCode': this.validateForm.value.expwayCode,
			'dataType': 2
		};
		this.eventsService.GetTrafficMeasureByEquipIdAndExpwayCode(FiveData).subscribe((r) => {

			const tempData: any = r;

			this.FiveMinList = tempData.Body.getTrafficMeasureByEquipIdAndExpwayCodeResponse.trafficMeasureList ? tempData.Body.getTrafficMeasureByEquipIdAndExpwayCodeResponse.trafficMeasureList : []
			this.FiveMinList.forEach((itemA) => {
				itemA.newParam = 'total';
				itemA.totalClass = parseInt(itemA.class1Count) + parseInt(itemA.class2Count) + parseInt(itemA.class3Count) + parseInt(itemA.class4Count) + parseInt(itemA.class5Count)
			});
			this.fiveData = this.FiveMinList[0] ? this.FiveMinList[0].dateTime : ''
			const tempA = [];
			const tempB = [];
			this.FiveMinList.forEach(item => {
				tempA.push(item.laneId);
			});
			tempA.sort();
			// console.log(tempA)
			tempA.forEach(landid => {
				this.FiveMinList.forEach(item => {
					if (landid === item.laneId) {
						tempB.push(item);
					}
				});
			});
			this.displayListFive = tempB;
		});
		// 一小时
		const SixtyData = {
			'equipId': this.validateForm.value.equipId,
			'expwayCode': this.validateForm.value.expwayCode,
			'dataType': 3
		};
		this.eventsService.GetTrafficMeasureByEquipIdAndExpwayCode(SixtyData).subscribe((r) => {
			// console.log('==============sixMin============');
			// console.log(SixtyData);
			// console.log(r);
			const tempData: any = r;
			// console.log(tempData.Body.getTrafficMeasureByEquipIdAndExpwayCodeResponse.trafficMeasureList)
			this.SixtyMinList = tempData.Body.getTrafficMeasureByEquipIdAndExpwayCodeResponse.trafficMeasureList ? tempData.Body.getTrafficMeasureByEquipIdAndExpwayCodeResponse.trafficMeasureList : []
			this.SixtyMinList.forEach((itemA) => {
				itemA.newParam = 'total';
				itemA.totalClass = parseInt(itemA.class1Count) + parseInt(itemA.class2Count) + parseInt(itemA.class3Count) + parseInt(itemA.class4Count) + parseInt(itemA.class5Count)
			});
			this.hourData = this.SixtyMinList[0] ? this.SixtyMinList[0].dateTime : ''
			const tempA = [];
			const tempB = [];
			this.SixtyMinList.forEach(item => {
				tempA.push(item.laneId);
			});
			tempA.sort();
			// console.log(tempA)
			tempA.forEach(landid => {
				this.SixtyMinList.forEach(item => {
					if (landid === item.laneId) {
						tempB.push(item);
					}
				});
			});
			this.displayListSixty = tempB;
		});
	}
	async initData() {
		const allType = await this.commonService.allType;
		this.expwayCodeList = allType[EquipType.EXPWAY_CODE];
		this.expwayDirection = allType[EquipType.EXPWAY_DIRECTION];
		this.getTestData();
		this.getAllData();
	}

	getTestData() {

		this.eventsService.GetAllEquipConfig().subscribe((r) => {
			// console.log('==================');
			// console.log(r);
			const tempData: any = r;
			this.listOfDataA = tempData.Body.getAllEquipConfigResponse.equipConfigDtoList;
			// console.log('=========listofdataA=========');
			// console.log(this.listOfDataA);
			this.formPatchValue();
		});
	}
	getAllData() {

		const queryData = {
			propertyCode: [0]
		};
		this.eventsService.GetTechnicalAlarmByEquipTypeAndExpwayCode(queryData).subscribe((r) => {
			// console.log(r);
			const tempData: any = r;
			this.listOfData = tempData.Body.getTechnicalAlarmByEquipTypeAndExpwayCodeResponse.technicalAlarmList;
			// console.log(this.listOfData);
			this.formPatchValue();
		});
	}
	mergeData(arrayA, arrayB): void {
		// console.log(arrayA);
		// console.log(arrayB);
		arrayA.forEach((itemA) => {
			arrayB.forEach((itemB) => {
				if (itemA.equipId === itemB.equipId) {
					// console.log('work');
					const data = { ...itemA, ...itemB };
					this.listOfDataB = [...this.listOfDataB, data];
				}
			});
		});
		// console.log(this.listOfDataB);
		this.listOfDataB.forEach((item) => {
			const tempDir = item.expwayCode + '-' + item.dir;
			this.expwayCodeList.forEach((dataA) => {
				if (item.expwayCode === dataA.value) {
					item.expWay = dataA.description;
				}
			});
			this.expwayDirection.forEach((dataB) => {
				if (tempDir === dataB.value) {
					item.direction = dataB.description;
				}
			});
			// 去重
		});
	}
}
