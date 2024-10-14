import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { EquipmentService } from '../equipment-control.service';
import { NzMessageService } from 'ng-zorro-antd';
import * as _ from 'lodash'
@Component({
	selector: 'emas-pmcs',
	templateUrl: './pmcs.component.html',
	styleUrls: ['./pmcs.component.css']
})
export class PmcsComponent implements OnInit {
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
	isVisibleMiddle = false;
	imgUrl = '';
	selectItem: any = '';
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	trActive: any;
	listOfData = [];
	listEquipType = [];
	equipType ;
	disabled = false;
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

	selectDisabled() {
		this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/dcss_noimage.png';
		switch (this.equipType.equipmentCode) {
			// LUS EQUIPMENT TYPES
			case 'lus':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/lus.png';
				break;
			case 'llk':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/LUS_PLCdisplay.png';
				break;
			case 'ltc':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/LUS_PLCdisplay.png';
				break;
			// FIRE EQUIPMENT TYPES
			case 'sap':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/fire.png';
				break;
			case 'map':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/fire.png';
				break;
			case 'gsm':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/fire.png';
				break;
			case 'fir':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/fire.png';
				break;
			// PMCS EQUIPMENT TYPES
			// 1. Ventilation System:
			case 'jfa':
			case 'tsf':
			case 'fpp':
			case 'fpe':
			// case 'tbf':
			//        this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/jfa.png';
			//        break;
			case 'ane':
			case 'cot':
			case 'coe':
			case 'vis':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/vis.png';
				break;
			// 2. Air Conditioning System:
			case 'air':
			case 'rtp':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/air.png';
				break;
			// 3. Lighting System
			case 'pho':
			case 'bsw':
			case 'blg':
			case 'lpa':
			case 'lln':
			case 'lls':
			case 'lsw':
			case 'lsn':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/bsw.png';
				break;
			// 4. Drainage System
			case 'tmp':
			case 'tms':
			case 'dwp':
			case 'dws':
			case 'clp':
			case 'cls':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/sump1.png';
				break;
			// 5. Doors & Staircase System
			case 'pdo':
			case 'vdo':
			case 'bre':
			case 'sta':
			case 'pfa':
			case 'nfa':
			case 'fct':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/fire.png';
				break;
			// 6. Power Control System
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
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/fb1.png';
				break;
			// 7. Communication System
			// case 'sos':
			case 'esp':
			case 'pet':
			case 'pew':
			case 'pav':
			case 'lsc':
			case 'pnw':
			case 'cup':
			case 'com':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/jfa.png';
				break;
			// 8. PLC System
			case 'tio':
			case 'bio':
			case 'nio':
			case 'pcc':
				this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/jfa.png';
				break;
			// case 'SUMP5':
			//        {
			//            this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/sump5.png';
			//            break;
			//        }
		}
		// 	case 'Visibility Meter':
		// 	case 'Tunnel Section Fan':
		// 	case 'Jet Fan':
		// 	case 'Fan Panel Power':
		// 	case 'Fan Panel Emergency':
		// 	case 'Carbon Monoxide Meter - Internal':
		// 	case 'Carbon Monoxide Meter - External':
		// 	case 'Anemometer': { this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/vis.png'; break; }
		// 	case 'Room Temperature':
		// 	case 'Split Air Con': { this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/air.png'; break; }
		// 	case 'Photometer':
		// 	case 'Boost Lighting Switching Unit':
		// 	case 'Boost Lighting Group':
		// 	case 'Line Lighting Switching Unit Plant':
		// 	case 'Line Lighting Control North':
		// 	case 'Line Lighting Control South':
		// 	case 'Line Lighting Switching : Signal LL':
		// 	case 'Lighting Panel': { this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/bsw.png'; break; }
		// 	case 'Tunnel Main Pump':
		// 	case 'Tunnel Main Sump':
		// 	case 'De-Watering Pump':
		// 	case 'De-Watering Sump':
		// 	case 'Cleaning Pump':
		// 	case 'Cleaning Sump': { this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/sump1.png'; break; }
		// 	case 'Pedestrian Door':
		// 	case 'Vehicle Door':
		// 	case 'Breaching Inlet Door':
		// 	case 'Emergency Staircase':
		// 	case 'Pressurization Fan':
		// 	case 'Normal Fan':
		// 	case 'Fire Cabinet in Tunnel': { this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/fire.png'; break; }
		// 	case 'HV Public Utility Incoming Feeder Voltage':
		// 	case 'HV Public Utility Incoming Feeder Current':
		// 	case 'HV Public Utility Incoming Feeder Power & Breaker':
		// 	case 'High Voltage Bus Coupler':
		// 	case 'High Voltage Outgoing Feeder':
		// 	case 'High Voltage Incoming Feeder From Other Building':
		// 	case 'High Voltage Transformer Feeder':
		// 	case 'High Voltage Transformer Monitoring':
		// 	case 'High Voltage DC Power Device':
		// 	case 'LV MDB Incoming Feeder':
		// 	case 'LV MDB Outgoing Feeder':
		// 	case 'LV MDB Under Voltage Relay':
		// 	case 'LV DC Power Device':
		// 	case 'LV Diesel Generator':
		// 	case 'LV Automatic Transfer Switch':
		// 	case 'LV Manual Transfer Switch':
		// 	case 'LV Emergency Panel Outgoing Feeder':
		// 	case 'LV UPS':
		// 	case 'LV UPS Normal Circuit Breaker':
		// 	case 'qqwLV UPS Maintenance Circuit Breakere':
		// 	case 'LV UPS Under Voltage Relay':
		// 	case 'LV Manual Transfer Switch with Trip Indication':
		// 	case 'LV Automatic Transfer Switch with Trip Indication':
		// 	case 'LV Bus Coupler':
		// 	case 'Intermediate Loop Feeder Cubicle Class':
		// 	case 'Intermediate Loop Feeder Cubicle New Class':
		// 	case 'Common Alarm In 22kV Switchgear':
		// 	case 'LV Annunciator Panel':
		// 	case 'LV Emergency Panel Under-Voltage Relay':
		// 	case 'LV Emergency Panel Incoming Feeder':
		// 	case 'HV Busbar Protection': { this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/fb1.png'; break; }
		// 	case 'SOS Phone':
		// 	case 'ES Phone':
		// 	case 'PMCS ETS':
		// 	case 'PMCS ETS Workstation':
		// 	case 'PMCS AVRPFS':
		// 	case 'PMCS Layer 3 Switch':
		// 	case 'PMCS NMS Workstation':
		// 	case 'CS Uninterruptible Power Supply':
		// 	case 'Communication Gateway': { this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/jfa.png'; break; }
		// 	case 'RIO In Tunnel':
		// 	case 'RIO In Building':
		// 	case 'RIO With Melsecnet/H Slave & Modbus Module':
		// 	case 'PLC Cluster': { this.imgUrl = '../../../../assets/img/Images/Screenshots_EquipmentControlTab/jfa.png'; break; }
		// }
	}
	// 请求下拉框数据
	getData() {
		this.equipmentService.getAllEquipType().subscribe((r) => {
			const tempData: any = r;
			const arr = tempData.Body.getAllEquipTypeResponse.equipTypeList;
			if (arr) {
				for (const item of arr) {
					if (item.felsCode === 'ax') {
						this.listEquipType.push(item);
					}
				}
				this.listEquipType = this.listEquipType.sort((a, b) => b.description.localeCompare(a.description));
				this.equipType = this.listEquipType[0];
			} else {
				this.message.create('error', `No Data`);
			}
			this.selectDisabled();
		});
	}
	equipTypeChange() {
		this.selectDisabled();
		this.listEquipType.forEach(item => {
			if (item.equipmentCode === this.equipType.equipmentCode) {
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
							// 新增一个键值对
							ele['idAndLoca'] = ele.equipmentId + '-' + ele.location;
						});
						this.listOfData = newData;
					} else {
						this.listOfData = [];
						this.message.create('error', `No Data`);
					}
					this.listOfDisplayData = [...this.listOfData];
				});
				return;
			}
		});
		//console.log('Now Data:',this.listOfData)
	}
	clickTr(idx, data): void {
		this.trActive = data.equipmentId;
		this.selectItem = data;
	}

	select() {
		if (this.selectItem) {
			this.isVisibleMiddle = true;
			//console.log('selectData:',this.selectItem)
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

	initForm() {
		this.validateForm = this.fb.group({
			equipType: [{ value: null, disabled: false }],
		});
	}
	submitForm(): void {
	}
}

