import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { EquipmentService } from '../equipment-control.service';
import { NzMessageService } from 'ng-zorro-antd';
import * as moment from 'moment';

@Component({
	selector: 'emas-surveillance-system',
	templateUrl: './surveillance-system.component.html',
	styleUrls: ['./surveillance-system.component.css']
})
export class SurveillanceSystemComponent implements OnInit {
	
	validateForm: FormGroup;
	selectItem: any = '';
	selectItemId: number;
	listOfData = [];
	equipType = '';
	imgUrl = '';
	disabled: boolean;
	sortName: string | null = null;
	sortValue: string | null = null;
	listOfDisplayData = [...this.listOfData];
	plcHost;
	plcHostList = [];

	constructor(
		private fb: FormBuilder,
		private equipmentService: EquipmentService,
		private message: NzMessageService) { }

	ngOnInit() {
		this.setImage();
		this.getHost();
		this.initForm();
		this.getData();
		this.runTime();
	}
	initForm() {
		this.validateForm = this.fb.group({
			plcHost: [{ value: this.plcHostList[0], disabled: false }]
		});
	}
	submitForm(): void {
		this.filterPlcHost();
	}
	setImage() {
		// 根据下拉框改变而改变图片
		this.equipType = 'wmc';
		if (this.equipType === 'wmc') {
			this.imgUrl = '../../../../assets/img/Images/SS_WaterMist/wms.png';
		} else {
			this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/dcss_noimage.png';
		}
	}

	getHost() {
		const queryData = {equipTypes: this.equipType};
		this.equipmentService.getPlcHostByEquipTypes(queryData).subscribe(item => {
			// console.log('plcHostList:', item)
			for (let element of item.data) {
				this.plcHostList.push(element);
			}
			this.plcHostList.unshift('All');
			this.plcHost = this.plcHostList[0];
		});
	}

	getData(auto?) {
		if (auto) {
			this.validateForm.patchValue({
				plcHost: this.plcHost,
			});
		} else {
			this.validateForm.patchValue({
				plcHost: this.plcHostList[0],
			});
		}
		const queryData = {
			equipType: this.equipType,
			attrCode: 'ope'
		}
		this.equipmentService.getWMSEquipStatusByEquipType(queryData).subscribe(item => {
			// console.log('got EquipList Data:', item)
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
			// this.listOfDisplayData = [...this.listOfData];
		});
	}

	filterPlcHost() {
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
		if (this.listOfData.length > 1) {
			this.listOfData.forEach((item, index) => {
				item.active = false;
				if (idx === index) {
					// 点击每个tr给与样式active
					item.active = true;
				}
			});
			// this.selectItemId = data.equipIdLocation;
			this.selectItem = data;
		} else {
			data.active = true;
		}
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

	runTime() {
		const nowPath = location.href;
		let pathList = nowPath.split("/");
		let tempPath = pathList[6];
		if (tempPath != 'surveillance-system') {
			return;
		}
		setTimeout(() => {
			const cteStatusUpdate = localStorage.getItem('cteStatusUpdate');
			if (cteStatusUpdate == '1') {
				localStorage.removeItem('cteStatusUpdate');
				// this.equipTypeChange(true);
				this.getData(true);
			}
			this.runTime();
		}, 1000);
	}
}
