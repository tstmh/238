import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { EquipmentService } from '../equipment-control.service';
import { NzMessageService } from 'ng-zorro-antd';
import * as moment from 'moment';

@Component({
	selector: 'emas-new-pmcs',
	templateUrl: './new-pmcs.component.html',
	styleUrls: ['./new-pmcs.component.css']
})
export class NewPmcsComponent implements OnInit {

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
	equipTypeList = [];
	plcHostList = [];
	validateForm: FormGroup;
	controlArray: any[] = [];
	isVisibleMiddle = false;
	selectItem: any = '';
	selectItemId: number;
	isCollapse = true;
	trActive: any;
	listOfData = [];
	processListData = [];
	listEquipType: Array<{ label: string; value: number }> = [
		{ label: 'equipType1', value: 1 },
		{ label: 'equipType2', value: 2 },
		{ label: 'equipType3', value: 3 }
	];
	equipType;
	plcHost;
	imgUrl = '';
	disabled: boolean;
	sortName: string | null = null;
	sortValue: string | null = null;
	listOfDisplayData = [...this.listOfData];
	constructor(
		private fb: FormBuilder,
		private equipmentService: EquipmentService,
		private message: NzMessageService) { }

	ngOnInit() {
		this.getData();
		this.initForm();
		this.runTime();
	}

	initForm() {
		this.validateForm = this.fb.group({
			equipType: [{ value: this.equipType, disabled: false }],
			plcHost: [{ value: this.plcHostList[0], disabled: false }]
		});
	}
	submitForm(): void {
		this.filterPlcHost();
	}
	selectDisabled() {
		// 根据下拉框改变而改变图片
		this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/dcss_noimage.png';
		switch (this.equipType) {
			case 'wmt':
				this.imgUrl = '../../../../assets/img/Images/SS_WaterMist/wms.png';
				break;
			case 'dep':
				this.imgUrl = '../../../../assets/img/Images/SS_WaterMist/FB2_lcp.png';
				break;
			case 'jop':
				this.imgUrl = '../../../../assets/img/Images/SS_WaterMist/FB3_lcp.png';
				break;
			case 'wiv':
				this.imgUrl = '../../../../assets/img/Images/SS_WaterMist/FB4_lcp.png';
				break;
		}
	}
	getData() {
		this.equipmentService.getWMSAllEquipType().subscribe(item => {
			// console.log('All equiptype:', item);
			for (let element of item.data.equipTypeVOList) {
				if (element.felsCode == 'ax') {
					this.equipTypeList.push(element);
				}
			}
			// for (let element of item.data.plcHostList) {
			// 	this.plcHostList.push(element);
			// }
			// this.plcHostList.unshift('All');
			this.equipTypeList = this.equipTypeList.sort((a, b) => b.type.localeCompare(a.type));
			this.equipType = this.equipTypeList[0].type;
			// this.plcHost = this.plcHostList[0];
			this.getplcHost();
			this.selectDisabled();
		});
	}
	getplcHost() {
		const query = {equipTypes: this.equipType};
		this.equipmentService.getPlcHostByEquipTypes(query).subscribe(item => {
			// console.log('got plcHost Data:', item)
			if (item.code == 0) {
				this.plcHostList = item.data;
				this.plcHostList.unshift('All');
				this.plcHost = this.plcHostList[0];
			} else {
				this.plcHostList = [];
				this.message.create('error', `No PlcHost Data`);
			}
		});
	}
	equipTypeChange(auto?) {
		if (auto) {
			this.validateForm.patchValue({
				plcHost: this.plcHost,
			});
		} else {
			this.validateForm.patchValue({
				plcHost: this.plcHostList[0],
			});
		}
		this.selectDisabled();
		const queryData = {
			equipType: this.equipType,
			attrCode: 'ope'
		}
		this.equipmentService.getWMSEquipStatusByEquipType(queryData).subscribe(item => {
			//console.log('got EquipList Data:', item)
			if (item.code == 0) {
				let dataList = item.data
				//console.log(dataList)
				this.listOfData = []
				for (let equip of dataList) {
					equip['idAndLoca'] = equip.equipId + '-' + equip.plcHost
					equip['dateString'] = moment(equip.updatedDate).format('DD/MM/YYYY HH:mm:ss A')
					this.listOfData.push(equip)
				}
			} else {
				this.listOfData = [];
				this.message.create('error', `No Data`);
			}
			this.filterPlcHost();
			//this.listOfDisplayData = [...this.listOfData];
		});
	}

	filterPlcHost() {
		// const { plcHost } = this.validateForm.value;
		// console.log('Before plcHost:', this.plcHost)
		// this.plcHost = plcHost;
		//console.log('Now plcHost:', this.plcHost)
		if (this.plcHost !== 'All') {
			let dataList = this.listOfData;
			let tempData = [];
			dataList.forEach(ele => {
				if (this.plcHost === ele.plcHost) {
					tempData.push(ele);
				}
			});
			this.listOfDisplayData = [...tempData];
		} else {
			this.listOfDisplayData = [...this.listOfData];
		}
		//console.log(this.listOfDisplayData)
	}

	clickTr(idx, data): void {
		this.trActive = data.equipId;
		this.selectItem = data;
	}
	showModalMiddle(): void {
		//this.isVisibleMiddle = true;  //for test
		if (this.selectItem) {
			this.isVisibleMiddle = true;
		} else {
			this.message.create('warning', `Please select an item`);
		}
	}
	closeModal(e) {
		this.isVisibleMiddle = false;
		// this.disabled = true;
		this.selectItem = '';
	}
	// sort data
	sort(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
	}
	search(): void {
		const data = this.listOfDisplayData;
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

	runTime() {
		const nowPath = location.href;
		let pathList = nowPath.split("/");
		let tempPath = pathList[6];
		if (tempPath != 'new-pmcs') {
			return;
		}
		setTimeout(() => {
			const cteStatusUpdate = localStorage.getItem('cteStatusUpdate');
			if (cteStatusUpdate == '1') {
				localStorage.removeItem('cteStatusUpdate');
				this.equipTypeChange(true);
				//this.filterPlcHost();
			}
			this.runTime();
		}, 1000);
	}

}