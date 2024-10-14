import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { EquipmentService } from '../equipment-control.service';
import { NzMessageService } from 'ng-zorro-antd';

@Component({
	// tslint:disable-next-line:component-selector
	selector: 'emas-lus',
	templateUrl: './lus.component.html',
	styleUrls: ['./lus.component.css']
})
export class LusComponent implements OnInit {
	imgUrl = '';
	validateForm: FormGroup;
	controlArray: any[] = [];
	isVisibleMiddle = false;
	selectItem: any = '';
	selectItemId: number;
	isCollapse = true;
	trActive: any;
	disabled: boolean;
	// 列表数据
	listOfData = [];
	// 下拉框
	listEquipType: Array<{ label: string; value: number }> = [
		{ label: 'LUS Link', value: 1 },
		{ label: 'LUS Controller', value: 2 },
		{ label: 'LUS', value: 3 }
	];
	equipTypeList = [];
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
	equipType = '';
	sortName: string | null = null;
	sortValue: string | null = null;
	listOfDisplayData = [...this.listOfData];
	constructor(
		private fb: FormBuilder,
		private equipmentService: EquipmentService,
		private message: NzMessageService,
	) { }

	ngOnInit() {
		this.initForm();
		this.getData();
	}

	initForm() {
		this.validateForm = this.fb.group({
			equipType: [{ value: null, disabled: false }],
		});
	}
	selectDisabled() {
		// 根据下拉框改变而改变图片
		if (this.equipType === 'LUS') {
			this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/lus.png';
		} else {
			this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/LUS_PLCdisplay.png';
		}
		if (this.equipType === 'LUS') {
			this.disabled = false;
		} else {
			this.disabled = true;
		}
	}
	// 请求默认数据
	getData() {
		this.equipmentService.getAllEquipType().subscribe((r) => {
			const tempData: any = r;
			const arr = tempData.Body.getAllEquipTypeResponse.equipTypeList;
			// 遍历出只含这三个字段的item
			for (const item of arr) {
				if (item.felsCode === 'al') {
					this.equipTypeList.push(item);
				}
			}
			// Deduplication -> 去重
			const map = new Map();
			this.equipTypeList = this.equipTypeList.filter(v => !map.has(v.equipmentCode) && map.set(v.equipmentCode, 1));
			// console.log('Equip Type:', this.equipTypeList);
			// 给下拉框一个默认值
			this.equipType = this.equipTypeList[2].description;
			this.selectDisabled();
		});
	}
	equipTypeChange() {
		this.selectDisabled();
		this.equipTypeList.forEach(item => {
			if (item.description === this.equipType) {
				const queryData = {
					felsCode: item.felsCode,
					equipType: item.equipmentCode,
					attrName: 'ope'
				};
				this.equipmentService.getEquipStatusByEquipType(queryData).subscribe((r) => {
					const tempData: any = r;
					if (tempData.Body.getEquipStatusByEquipTypeResponse.equipStatusList) {
						const newData = tempData.Body.getEquipStatusByEquipTypeResponse.equipStatusList;
						// 根据下拉框的值改变不同的listOfData
						newData.forEach(ele => {
							if (queryData.equipType === 'lus') {
								ele['idAndLoca'] = ele.equipmentId + '-' + ele.location;
							} else if (queryData.equipType === 'llk') {
								const s = ele.equipmentId;
								ele['idAndLoca'] = ele.equipmentId + '-' + 'Station' + s[2] + 'Port' + s[3];
							} else {
								ele['idAndLoca'] = ele.equipmentId;
								ele['lastModified'] = '0001-01-01T12:48:37.227+08:00';
							}
						});
						this.listOfData = newData;
					} else {
						this.listOfData = [];
						this.message.create('error', `No Data`);
					}
					this.listOfDisplayData = [...this.listOfData];
				});
			}
		});
	}
	submitForm(): void {
	}

	clickTr(idx, data): void {
		this.trActive = data.equipmentId;
		this.selectItem = data;
	}
	showModalMiddle(): void {
		if (this.selectItem) {
			this.isVisibleMiddle = true;
		} else {
			this.message.create('warning', `Please select an item`);
		}
	}
	closeModal(e) {
		this.selectItem.isCheck = false;
		this.isVisibleMiddle = false;
		this.selectItem = '';
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

}
