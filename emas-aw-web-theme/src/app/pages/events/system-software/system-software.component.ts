import { AfterContentInit, AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { EventsService } from '../events.service';
import { CommonService } from '../../../service/common.service';

@Component({
	// tslint:disable-next-line: component-selector
	selector: 'emas-system-software',
	templateUrl: './system-software.component.html',
	styleUrls: ['./system-software.component.css']
})
export class SystemSoftwareComponent implements AfterContentInit {
	renderHeader = [
		{
			name: 'Application',
			key: null,
			value: 'application',
			isChecked: true
		},
		{
			name: 'Main',
			key: null,
			value: 'main',
			isChecked: true
		},
		{
			name: 'StandBy',
			key: null,
			value: 'standBy',
			isChecked: true
		},
		{
			name: 'Start Date',
			key: null,
			value: 'startDate',
			isChecked: true
		}
	];

	tapList: any = [];
	dmaList: any = [];
	nmsList: any = [];
	lusList: any = [];
	axList: any = [];
	firList: any = [];
	itList: any = [];
	eciList: any = [];
	dciList: any = [];
	sciList: any = [];
	tciList: any = [];
	wciList: any = [];
	dcaList: any = [];
	cssList: any = [];
	hmsList: any = [];
	meaList: any = [];
	imsList: any = [];
	idiList: any = [];
	idaList: any = [];
	dcsList: any = [];
	da1List: any = [];
	da2List: any = [];
	pmcList: any = [];
	hmaList: any = [];
	// statusList: any = [];
	typeList = ['tap', 'dma', 'nms', 'lus', 'ax', 'fir', 'it', 'eci', 'dci', 'sci', 'tci', 'wci', 'dca', 'css', 'hms', 'mea', 'ims', 'ida', 'idi', 'dcs', 'da1', 'da2', 'pmc', 'hma'];

	listOfData ;
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;

	constructor(
		private fb: FormBuilder,
		private eventsService: EventsService,
		private commonService: CommonService
	) { }

	ngAfterContentInit() {
		this.getAllData();
	}

	getAllData() {
		this.typeList.forEach((item) => {
			this[item + 'List'] = this.commonService.equipTypeObject[item];
			// this.statusList.push(this[item + 'List']);
		});
		// console.log('hmaList:', this.hmaList);	//HMS Application have 2 servers
		// console.log('meaList:', this.meaList);	//MFELS Application have 4 servers
		// console.log('statusList:', this.statusList);
		this.eventsService.getSoftwareModuleLocation().subscribe((e: any) => {
			const res = e.Body.getSoftwareModuleLocationResponse?.softwareModuleLocationList || [];
			this.commonService.getEquipConfig$().subscribe(equipConfigs => {
				const equipIdMap = {};
				res.forEach(software => {
					const value = equipIdMap[software.equipId] || {};
					value.activeDateTime = software.active ? software.statusDateTime : '-NA-';
					for (const equipConfig of equipConfigs) {
						if (software.equipId === equipConfig.equipId) {
							value.application = equipConfig.equipDesc;
						}
						if (software.runEquipId === equipConfig.equipId) {
							software.runDesc = equipConfig.equipDesc;
						}
						if (value.application && software.runDesc) {
							break;
						}
					}
					if (software.clusterType === 'MAIN') {
						value.mainDesc = software.runDesc;
						value.status = software.active;
					} else {
						value.standByDesc = software.runDesc;
						value.standByStatus = software.active;
					}
					equipIdMap[software.equipId] = value;
				});
				this.listOfData = Object.values(equipIdMap);
			});
		});
	}
}
