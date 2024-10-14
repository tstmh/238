import { Injectable, NgZone } from '@angular/core';
import { wsSend } from '../public/utils/webservices';
import { Subject, Observable, BehaviorSubject, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
	providedIn: 'root'
})
export class CommonService {
	private equipConfig: EquipConfig[];
	private _allType;
	equipTypeObject: any = {
		ces: [],
		mes: [],
		isw: [],
		cds: [],
		dmc: [],
		dbs: [],
		tap: [],
		lus: [],
		dma: [],
		nms: [],
		ax: [],
		fir: [],
		it: [],
		eci: [],
		dci: [],
		sci: [],
		tci: [],
		wci: [],
		dca: [],
		css: [],
		hms: [],
		mea: [],
		ims: [],
		cfels: [],
		ticss: [],
		dcss: [],
		scss: [],
		wcss: [],
		group1: [],
		nfw: [],
		few: [],
		tcw: [],
		jfw: [],
		jeyes: [],
		bfw: [],
		bcw: [],
		esw: [],
		rcs: [],
		up2: [],
		nwt: [],
		awt: [],
		bks: [],
		fcw: [],
		bkw: [],
		rou: [],
		tas: [],
		ecs: [],
		vtl: [],
		asw: [],
		idi: [],
		ida: [],
		dcs: [],
		da1: [],
		da2: []
	};
	private communicationWithNetwork = new Subject<any>();
	constructor(private zone: NgZone) {
		this.GetBackEndEquipment();
	}

	public getEquipConfig$() {
		if (!this.equipConfig) {
			const request$ = wsSend('getAllEquipConfig', {}, 'CommonWebService');
			request$.subscribe((res: any) => {
				this.equipConfig = res.Body.getAllEquipConfigResponse.equipConfigDtoList;
			});
			return request$.pipe(map((res: any) => res.Body.getAllEquipConfigResponse.equipConfigDtoList));
		}
		return of(this.equipConfig);
	}


	public get allType(): Promise<any> {
		const promise = new Promise((resolve) => {
			if (!this._allType) {
				wsSend('getAllCommonTypeConfig', {}, 'CommonWebService')
					.pipe(map(this.initAllCommonType)).subscribe(res => {
						this._allType = res;
						// For ExpWay have J-eyes issues, do filter 80
						let expList = [];
						this._allType.expway_code.forEach(ele => {
							if (ele.value != '80') {
								expList.push(ele);
							}
						});
						this._allType.expway_code = expList;
						resolve(this._allType);
						// resolve(res);	// Old
					});
			} else {
				resolve(this._allType);
			}
		});
		return promise;
	}

	initAllCommonType(res) {
		const typeList = res.Body.getAllCommonTypeConfigResponse.commonTypeConfigList;
		const arr = {};
		typeList.sort((a, b) => a.description.localeCompare(b.description))
			.forEach(item => {
				if (arr[item.name]) {
					arr[item.name].push(item);
				} else {
					arr[item.name] = [item];
				}
			});
		return arr;
	}

	filterEquipType(location, subSystemValue) {
		const promise = new Promise((resolve) => {
			this.getEquipConfig$().subscribe(list => {
				const equipTypeSet = new Set();
				list.forEach(item => {
					if (location === 1) { // 'Backend'
						if ((item.propertyCode == 1 || item.propertyCode == 2)
							&& (item.subSystemId == subSystemValue || subSystemValue === 'All')) {
							equipTypeSet.add(item.equipType);
						}
					} else if (item.propertyCode == 0
						&& (item.subSystemId == subSystemValue || subSystemValue === 'All')) {
						equipTypeSet.add(item.equipType);
					}
				});
				resolve([...equipTypeSet].sort());
			});

		});
		return promise;
	}

	getEquipConfigById(id): EquipConfig {
		for (const item of this.equipConfig) {
			if (item['equipId'] === id) {
				return item;
			}
		}
	}
	public SendMessage(message: any) {
		this.communicationWithNetwork.next(message);
	}
	public GetMessage(): Observable<any> {
		return this.communicationWithNetwork.asObservable();
	}

	GetBackEndEquipment() {
		wsSend('getBackEndEquipment', {}, 'AWWebService').subscribe((r) => {
			const res: any = r;
			const equipTypeList = res.Body.getBackEndEquipmentResponse.equipStatusList;
			equipTypeList.forEach(item => {
				let array = [];
				if (this.equipTypeObject[item.equipType]) {
					if (item.equipType === 'dcs') {
						if (item.equipId === 'dcs_a1') {
							item.equipType = 'da1';
						} else if (item.equipId === 'dcs_a2') {
							item.equipType = 'da2';
						}
					}
					array = this.equipTypeObject[item.equipType];
				}
				this.equipTypeObject[item.equipType] = array;
				array.push(item);
			});
			this.SendMessage({
				type: 'eqtTypeChange',
				data: this.equipTypeObject
			});
		});
	}

}

export enum EquipType {
	EXPWAY_CODE = 'expway_code',
	EMAS_SUBSYSTEM = 'emas_subsystem',
	EXPWAY_DIRECTION = 'expway_direction',
	ALERT_TYPE = 'traffic_alert',
	EQUIP_TYPE = 'equip_type',
	VMS_FONTTYPE = 'vms_font_type',
	VMS_PICGROUP = 'vms_pic_group',
	TRAFFIC_MEASURE = 'lane_type',
	VMS_EQUIP_TYPE = 'vms_equip_type',
	VMS_MSG_CATEGORY = 'vms_msg_category',
	VMS_PAGE_MODE = 'vms_page_mode',
	VMS_TOGGLE_MODE = 'vms_toggle_mode',
	VMS_DISPLAY_MODE = 'vms_display_mode',
	VMS_FAN_CONTROL_MODE = 'vms_fan_control_mode',
	VMS_DIMMING_MODE = 'vms_dimming_mode'
}


interface EquipConfig {
	dir;
	enable;
	equipDesc;
	equipId;
	equipType;
	ipAddress;
	kmMarking;
	latitude;
	longitude;
	propertyCode;
	propertyDescription;
	subSystemId;
	systemId;
	trafficDataEnabled;
	expwayCode;
	phase;
}