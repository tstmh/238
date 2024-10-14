import { async } from '@angular/core/testing';

import { Component, OnInit, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ConfigService } from '../config.service';
import { CommonService, EquipType } from '../../../service/common.service';
import { ConstantsService } from '../../../service/constants.service';
import { NzMessageService, NzListItemMetaComponent } from 'ng-zorro-antd';
import { UserService } from '../../user-management/user-management.service';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';

@Component({
	// tslint:disable-next-line:component-selector
	selector: 'emas-equipment',
	templateUrl: './equipment.component.html',
	styleUrls: ['./equipment.component.css']
})
export class EquipmentComponent implements OnInit, AfterViewInit {

	renderHeader = [
		{
			name: 'Equip Type',
			key: null,
			value: 'equipType',
			isChecked: true
		},
		{
			name: 'Equip ID',
			key: null,
			value: 'equipID',
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
			name: 'FirmWare Version',
			key: null,
			value: 'firmwareVersion',
			isChecked: true
		}
	];
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	listOfData = [];
	listSubSystem: any = [
		this.myConstants.defValueForSelect
	];
	listEquipType: any = [
		this.myConstants.defValueForSelect
	];
	equipType = '';

	expwayCodeList = [];
	expwayDirection = [];
	commonTypeConfig = [];
	arrB = [];

	sortName: string | null = null;
	sortValue: string | null = null;
	listOfDisplayData = [...this.listOfData];

	constructor(
		private fb: FormBuilder,
		private configService: ConfigService,
		private commonService: CommonService,
		private message: NzMessageService,
		private userService: UserService,
		private changeDetectorRef: ChangeDetectorRef,
		private myConstants: ConstantsService
	) { }

	ngOnInit() {
		this.initForm();
		this.getData();
	}

	ngAfterViewInit() {

	}

	async getData() {
		const allType = await this.commonService.allType;
		this.expwayDirection =  allType[EquipType.EXPWAY_DIRECTION];
		const subSystem = allType[EquipType.EMAS_SUBSYSTEM];
		this.expwayCodeList = allType[EquipType.EXPWAY_CODE];
		subSystem.forEach((item) => {
			this.listSubSystem.push(item.value);
		});
		// this.validateForm.value.subSystem = this.listSubSystem[0];
		this.configService.GetAllEquipConfig().subscribe(r => {
			const searchData: any = r;
			const newData = searchData.Body.getAllEquipConfigResponse.equipConfigDtoList;
			this.listOfData = [...newData];
			this.listOfDisplayData = this.listOfData;
			this.changeData();
			newData.forEach(item => {
				this.arrB.push(item.equipType);
			});
			this.arrB = Array.from(new Set(this.arrB));

			this.arrB.forEach((item) => {
				// const data = {
				// 	description: item,
				// 	value: item
				// };
				this.listEquipType.push(item);
			});
			this.validateForm.patchValue({
				equipType: this.listEquipType[0]
			});
			// this.equipType = this.listEquipType[0].value;
			const ttt: number = this.listOfDisplayData.length / 10;
			const yu = this.listOfDisplayData.length % 10;
			if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			} else {
				// localStorage.setItem('totalPage', ttt.toFixed(0));
				this.userService.$totalPage.next(ttt.toFixed(0));
			}
		});
	}


	subsystemChange() {
		this.configService.GetAllEquipConfig().subscribe(r => {

			const searchData: any = r;
			let newData = searchData.Body.getAllEquipConfigResponse.equipConfigDtoList;
			// this.listOfDisplayData = [...newData];
			if (this.validateForm.value.subSystem !== this.myConstants.defValueForSelect) {
				const arr = [];
				newData.forEach(item => {
					if (item.subSystemId === this.validateForm.value.subSystem) {
						arr.push(item);
					}
				});
				this.listEquipType = [];
				arr.forEach(item => {
					this.listEquipType.push(item.equipType);
				});
				const a = Array.from(new Set(this.listEquipType));
				this.listEquipType = [this.myConstants.defValueForSelect, ...a];
				this.validateForm.patchValue({
					equipType: this.listEquipType[0]
				});
			} else {
				newData = newData.map(item => item.equipType);
				newData = Array.from(new Set(newData));
				this.listEquipType = [this.myConstants.defValueForSelect, ...newData];

			}
		});
	}
	changeData() {
		// console.log(this.expwayCodeList);
		// expway   kmMarking
		this.listOfDisplayData.forEach(item => {
			item.expway = '-NA-';
			this.expwayCodeList.forEach(value => {
				if (item.propertyCode !== '0') {
					item.kmMarking = '-NA-';
				} else if (item.propertyCode === '0') {
					item.kmMarking = this.returnFloat(item.kmMarking);
				} else if (item.expwayCode === value.value) {
					item.expway = value.description;
				} else if (!item.firmwareVersion) {
					item.firmwareVersion = '';
				}
			});
		});
		// direction
		this.configService.GetAllCommonTypeConfig().subscribe(r => {
			this.listOfDisplayData.forEach(item => {
				item.direction = '-NA-';
				const res: any = r;
				this.commonTypeConfig = res.Body.getAllCommonTypeConfigResponse.commonTypeConfigList;
				this.commonTypeConfig.forEach(value => {
					if (value.value === item.expwayCode + '-' + item.dir) {
						item.direction = value.description;
					}
				});
			});
		});
	}
	// 返回的数字保留到小数点后两位
	returnFloat(kmMarking) {
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
	}

	submitForm() {
		const subSystemValue = this.validateForm.value.subSystem;
		const equipTypeValue = this.validateForm.value.equipType;
		if (subSystemValue && equipTypeValue) {
			this.configService.GetAllEquipConfig().subscribe(r => {
				const searchData: any = r;
				const newData = searchData.Body.getAllEquipConfigResponse.equipConfigDtoList;
				this.listOfDisplayData = [];
				const a = [];
				if (subSystemValue === this.myConstants.defValueForSelect && equipTypeValue === this.myConstants.defValueForSelect) {
					this.getData();
				}
				newData.forEach(item => {
					if (item.subSystemId === subSystemValue && item.equipType === equipTypeValue) {
						a.push(item);
					} else if (subSystemValue === this.myConstants.defValueForSelect && equipTypeValue === item.equipType) {
						a.push(item);
					} else if (subSystemValue === item.subSystemId && equipTypeValue === this.myConstants.defValueForSelect) {
						a.push(item);
					}
				});
				this.listOfDisplayData = [...a];
				this.changeData();
				// tslint:disable-next-line:no-shadowed-variable
				const ttt: number = this.listOfDisplayData.length / 10;
				// tslint:disable-next-line:no-shadowed-variable
				const yu = this.listOfDisplayData.length % 10;
				if (yu < 5) {
					// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
					this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
				} else {
					// localStorage.setItem('totalPage', ttt.toFixed(0));
					this.userService.$totalPage.next(ttt.toFixed(0));
				}
			});
			const ttt: number = this.listOfDisplayData.length / 10;
			const yu = this.listOfDisplayData.length % 10;
			if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			} else {
				// localStorage.setItem('totalPage', ttt.toFixed(0));
				this.userService.$totalPage.next(ttt.toFixed(0));
			}
		}
	}

	initForm() {
		this.validateForm = this.fb.group({
			subSystem: [{ value: this.myConstants.defValueForSelect, disabled: false }],
			equipType: [{ value: this.myConstants.defValueForSelect, disabled: false }],
		});
	}
	mergeData(sourceData): void { // 获取expway 和 direction
		sourceData.forEach((item) => {
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
		});
	}
	pageChange(e) {
		this.userService.$addData.next(e);
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
		const ttt: number = this.listOfDisplayData.length / 10;
		const yu = this.listOfDisplayData.length % 10;
		if (yu < 5) {
			// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
			this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
		} else {
			// localStorage.setItem('totalPage', ttt.toFixed(0));
			this.userService.$totalPage.next(ttt.toFixed(0));
		}
	}
}
