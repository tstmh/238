import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CommonService } from '../../../service/common.service';
import { EquipmentService } from '../equipment-control.service';
import { NzMessageService, en_US } from 'ng-zorro-antd';

@Component({
	// tslint:disable-next-line:component-selector
	selector: 'ec-pubilc-table',
	templateUrl: './ec-pubilc-table.component.html',
	styleUrls: ['./ec-pubilc-table.component.css']
})
export class EcPubilcTableComponent implements OnInit {


	selectItem: any = {};

	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	@Input() listOfData = [];
	@Input() equipSelect;
	sortName: string | null = null;
	sortValue: string | null = null;
	disableSelect = true;
	isVisibleMiddle = false;
	constructor(
		private fb: FormBuilder,
		private equipmentService: EquipmentService,
		private commonService: CommonService,
		private message: NzMessageService,
	) { }

	ngOnInit() {
	}

	// sort data
	sort(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
	}
	search(): void {
		if (this.sortName && this.sortValue) {
			this.listOfData = this.listOfData.sort((a, b) =>
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
		}
	}
	clickTr(data): void {
		this.selectItem = data;
		this.check();
	}
	closeModal(e) {
		this.selectItem.isCheck = false;
		this.isVisibleMiddle = false;
		this.selectItem = '';
	}
	select() {
		if (this.selectItem) {
			this.isVisibleMiddle = true;
		} else {
			this.message.create('warning', `Please select an item`);
		}
	}
	check() {
		if (this.selectItem.equipmentCode) {
			switch (this.selectItem.equipmentCode) {
				case 'lus':
				case 'jfa':
				case 'tsf':
				case 'ane':
				case 'cot':
				case 'coe':
				case 'vis':
				case 'fpp':
				case 'fpe':
				// case 'tbf':
				// Air Conditioning System:(2)
				case 'air':
				case 'rtp':
				// Lighting System(8)
				case 'pho':
				case 'bsw':
				case 'blg':
				case 'lpa':
				case 'lln':
				case 'lls':
				case 'lsw':
				case 'lsn':
				// Drainage System(6)
				case 'tmp':
				case 'tms':
				case 'dwp':
				case 'dws':
				case 'clp':
				case 'cls':
				// Doors & Staircase System(7)
				case 'pdo':
				case 'vdo':
				case 'bre':
				case 'sta':
				case 'pfa':
				case 'nfa':
				case 'fct':
				// Power Control System(31)
				case 'hiv':
				case 'hic':
				case 'hip':
				case 'hvb':
				case 'hvo':
				case 'hio':
				case 'hvf':
				case 'hvm':
				case 'hvp':
				case 'lmi':
				case 'lmo':
				case 'uvr':
				case 'dcp':
				case 'gen':
				case 'ats':
				case 'mts':
				case 'epo':
				case 'ups':
				case 'unb':
				case 'umb':
				case 'upv':
				case 'mtt':
				case 'att':
				case 'bcp':
				case 'hbc':
				case 'hbd':
				case 'cas':
				case 'ann':
				case 'epu':
				case 'epi':
				case 'hbb':
				// Communication System(9)
				// case 'sos':
				case 'esp':
				case 'pet':
				case 'pew':
				case 'pav':
				case 'lsc':
				case 'pnw':
				case 'cup':
				case 'com':
				// 8. PLC System(4)
				case 'tio':
				case 'bio':
				case 'nio':
				case 'pcc':
					this.disableSelect = false;
					break;
			}
		} else {

			switch (this.equipSelect) {
				//LUS
				case 'llk':
				case 'ltc':
					this.disableSelect = true;
					break;
			}
		}
	}
}
