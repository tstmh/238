import { EquipType } from './../../../service/common.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CommonService } from "../../../service/common.service";

@Component({
	selector: 'emas-equipment-housing',
	templateUrl: './equipment-housing.component.html',
	styleUrls: ['./equipment-housing.component.css']
})
export class EquipmentHousingComponent implements OnInit {
	renderHeader = [
		{
			name: 'EquipID',
			key: null,
			value: 'equipID',
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

	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	isVisibleMiddle = false;
	selectItem: any = '';
	selectItemId: number;
	listOfData = [
		{
			equipID: 'MDB_811001',
			status: 'Operational',
			time: '9/26/2019 10:29:20 AM'
		},
		{
			equipID: 'MDB_811002',
			status: 'Non-Operational',
			time: '9/26/2019 10:29:20 AM'
		},
		{
			equipID: 'TEL_911001',
			status: 'Operational',
			time: '9/26/2019 10:29:20 AM'
		},
		{
			equipID: 'TEL_911002',
			status: 'Operational',
			time: '9/26/2019 10:29:20 AM'
		},
		{
			equipID: 'IDS_711001',
			status: 'Operational',
			time: '9/26/2019 10:29:20 AM'
		},
		{
			equipID: 'IDS_711002',
			status: 'Operational',
			time: '9/26/2019 10:29:20 AM'
		}
	];

	listExpWay = [
		{ label: 'All', value: 0 },
	];

	listEquipHousingID = [
		{ label: 'All', value: 0 },
		{ label: 'EH_711001', value: 1 },
		{ label: 'EH_711002', value: 2 },
		{ label: 'EH_711003', value: 3 },
		{ label: 'EH_711004', value: 4 },
	];
	listEquipType = [
		{ label: 'All', value: 0 },
		{ label: 'MDBPower', value: 1 },
		{ label: 'TelcoLine', value: 2 },
		{ label: 'IDS', value: 3 }
	];
	sortName: string | null = null;
	sortValue: string | null = null;
	constructor(
		private fb: FormBuilder,
		private commonService: CommonService
	) { }

	ngOnInit() {
		this.initForm();
		this.getSelectData();
	}

	initForm() {
		this.validateForm = this.fb.group({
			expWay: [{ value: null, disabled: false }],
			equipHousingID: [{ value: null, disabled: false }],
			equipType: [{ value: null, disabled: false }],
		});
	}
	submitForm(): void {
	}


	async getSelectData() {
		const allType = await this.commonService.allType;
		allType[EquipType.EXPWAY_CODE].forEach((item) => {
			this.listExpWay.push({ label: item.description, value: item.value });
		});
	}

	choice(item): void {
		this.selectItemId = item.EquipID;
		this.selectItem = item;
	}
	showModalMiddle(): void {
		this.isVisibleMiddle = true;
	}
	closeModal(e) {
		this.isVisibleMiddle = false;
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

}
