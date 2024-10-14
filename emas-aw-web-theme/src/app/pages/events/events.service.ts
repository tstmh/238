import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Bully, BullySubjectService } from 'src/app/share/service/bully-subject.service';
import { httpGet, ResultVO, wsSend } from 'src/app/public/utils/webservices';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class EventsService {
	public data: any;
	constructor(

		private router: Router,
		private bully: BullySubjectService,
		private httpClient: HttpClient
	) {
	}

	/**
	 * Technical Alarm
	 */
	GetTechnicalAlarmByEquipTypeAndExpwayCode(data) {
		return wsSend('getTechnicalAlarmByEquipTypeAndExpwayCode', data, 'AWWebService');
	}

	GetAllEquipConfig() {
		return wsSend('getAllEquipConfig', {}, 'CommonWebService');
	}

	GetAllCommonTypeConfig() {
		return wsSend('getAllCommonTypeConfig', {}, 'CommonWebService');
	}

	GetTrafficMeasureByEquipIdAndExpwayCode(data) {
		return wsSend('getTrafficMeasureByEquipIdAndExpwayCode', data, 'AWWebService');
	}
	AW_CFELS_TechAlarmAck(dtoListArry) {
		return wsSend('AW_CFELS_TechAlarmAck', { 'techAlarmAckDtoList': { dtoList: dtoListArry } }, 'CMHWebService');
	}
	AW_CFELS_TechAlarmClear(dtoListArry, clearBy) {
		return wsSend('AW_CFELS_TechAlarmClear', { 'technicalAlarmList': { dtoList: dtoListArry }, clearBy }, 'CMHWebService');
	}
	// 获取user-roll数据
	FindAllRoles() {
		return wsSend('findAllRoles', {}, 'RoleWebService');
	}

	getTrafficAlertByEquipIdAndExpwayCode(parameter) {
		return wsSend('getTrafficAlertByEquipIdAndExpwayCode', parameter, 'AWWebService');
	}

	getTrafficAlertByAlertId(parameter) {
		return wsSend('getTrafficAlertByAlertId', parameter, 'AWWebService');
	}

	getEquipConfigBySubSystem(subSystem) {
		return wsSend('getEquipConfigBySubSystem', { 'subSystem': subSystem }, 'CommonWebService');
	}

	getEquipTypeBySubSytem(subSystem) {
		return wsSend('getEquipTypeBySubSytem', { 'subSystem': subSystem }, 'CommonWebService');
	}

	getTrafficAlertByAlertTypeAndExpwayCode(parameter) {
		return wsSend('getTrafficAlertByAlertTypeAndExpwayCode', parameter, 'AWWebService');
	}

	getTrafficAlertByAlertTimeAndExpwayCode(parameter) {
		return wsSend('getTrafficAlertByAlertTimeAndExpwayCode', parameter, 'AWWebService');
	}

	getAIVideoByEquipIdAndExpwayCode(parameter) {
		return wsSend('getAIVideoByEquipIdAndExpwayCode', parameter, 'AWWebService');
	}

	getAIVideoByAlertTypeAndExpwayCode(parameter) {
		return wsSend('getAIVideoByAlertTypeAndExpwayCode', parameter, 'AWWebService');
	}

	getAIVideoByAlertTimeAndExpwayCode(parameter) {
		return wsSend('getAIVideoByAlertTimeAndExpwayCode', parameter, 'AWWebService');
	}

	getAIImageByEquipIdAndExpwayCode(parameter) {
		return wsSend('getAIImageByEquipIdAndExpwayCode', parameter, 'AWWebService');
	}

	getAIImageByAlertTypeAndExpwayCode(parameter) {
		return wsSend('getAIImageByAlertTypeAndExpwayCode', parameter, 'AWWebService');
	}

	getAIImageByAlertTimeAndExpwayCode(parameter) {
		return wsSend('getAIImageByAlertTimeAndExpwayCode', parameter, 'AWWebService');
	}

	getAllTechAlarmConfig() {
		return wsSend('getAllTechAlarmConfig', {}, 'CommonWebService');
	}

	getBackEndEquipment() {
		return wsSend('getBackEndEquipment', {}, 'AWWebService');
	}
	getSoftwareModuleLocation() {
		return wsSend('getSoftwareModuleLocation', {}, 'AWWebService');
	}
	AW_CFELS_TrafficAlertAck(dtoListArry) {
		return wsSend('AW_CFELS_TrafficAlertAck', { 'trafficAlertAckList': { dtoList: dtoListArry } }, 'CMHWebService');
	}

	// Field Equip 列表
	getEquipStatusByEquipTypeAndExpwayCode(param) {
		return wsSend('getEquipStatusByEquipTypeAndExpwayCode', param, 'AWWebService');
	}

	getAllFelsCode() {
		return wsSend('getAllFelsCode', {}, 'CTETunDataService');
	}

	getAllEquipType() {
		return wsSend('getAllEquipType', {}, 'CTETunDataService');
	}

	getFelsCodeBySubsystemId(param: object): Observable<ResultVO> {
		return httpGet(this.httpClient, 'WaterMistService', 'getFelsCodeBySubsystemId', param);
	}

	getSelectList(arr, key, listName) {
		const map = new Map();
		arr.forEach((item) => {
			if (!map.has(item[key]) && item[key]) {
				map.set(item[key], item);
			}
		});
		const uniqueList = [...map.values()];
		uniqueList.forEach((data) => {
			listName.push({ label: data[key], value: data[key] });
		});
	}
	covertToImage(subSystem, expWay) {
		let imagepath = `/assets/img/Images/SS_EventsTab/dcss_noimage.png`;

		// Add by new version
		const expWayList = [{code: '71', value: 'cte'},
							{code: '72', value: 'bke'},
							{code: '73', value: 'ecp'},
							{code: '74', value: 'aye'},
							{code: '75', value: 'piee'},
							{code: '76', value: 'piew'},
							{code: '77', value: 'tpe'},
							{code: '78', value: 'kje'},
							{code: '79', value: 'sle'}];
		const havePicSubList = ['dcss', 'scss', 'ticss', 'wcss'];
		let expValue = '';
		let isHaveSub = false;
		expWayList.forEach(ele => {
			if (ele.code == expWay) {
				expValue = ele.value;
			}
		});
		havePicSubList.forEach(ele => {
			if (ele == subSystem) {
				isHaveSub = true;
			}
		})

		if (subSystem && expValue && isHaveSub) {
			imagepath = `/assets/img/Images/SS_FieldEquip/${subSystem}-${expValue}.png`;
		}

		// for old version
		// try {
		// 	if (subSystem === 'dcss') {
		// 		if (expWay.indexOf('CTE') >= 0 || expWay === '71') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-cte.png`;
		// 		} else if (expWay === 'BKE' || expWay === '72') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-bke.png`;
		// 		} else if (expWay === 'ECP' || expWay === '73') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-ecp.png`;
		// 		} else if (expWay === 'AYE' || expWay === '74') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-aye.png`;
		// 		} else if (expWay === 'PIEE' || expWay === '75') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-piee.png`;
		// 		} else if (expWay === 'PIEW' || expWay === '76') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-piew.png`;
		// 		} else if (expWay === 'TPE' || expWay === '77') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-tpe.png`;
		// 		} else if (expWay === 'KJE' || expWay === '78') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-kje.png`;
		// 		} else if (expWay === 'SLE' || expWay === '79') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/dcss-sle.png`;
		// 		}

		// 	} else if (subSystem === 'scss') {
		// 		if (expWay.indexOf('CTE') >= 0 || expWay === '71') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-cte.png`;
		// 		} else if (expWay === 'BKE' || expWay === '72') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-bke.png`;
		// 		} else if (expWay === 'ECP' || expWay === '73') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-ecp.png`;
		// 		} else if (expWay === 'AYE' || expWay === '74') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-aye.png`;
		// 		} else if (expWay === 'PIEE' || expWay === '75') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-piee.png`;
		// 		} else if (expWay === 'PIEW' || expWay === '76') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-piew.png`;
		// 		} else if (expWay === 'TPE' || expWay === '77') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-tpe.png`;
		// 		} else if (expWay === 'KJE' || expWay === '78') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-kje.png`;
		// 		} else if (expWay === 'SLE' || expWay === '79') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/scss-sle.png`;
		// 		}
		// 	} else if (subSystem === 'ticss') {
		// 		if (expWay.indexOf('CTE') >= 0 || expWay === '71') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-cte.png`;
		// 		} else if (expWay === 'BKE' || expWay === '72') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-bke.png`;
		// 		} else if (expWay === 'ECP' || expWay === '73') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-ecp.png`;
		// 		} else if (expWay === 'AYE' || expWay === '74') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-aye.png`;
		// 		} else if (expWay === 'PIEE' || expWay === '75') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-piee.png`;
		// 		} else if (expWay === 'PIEW' || expWay === '76') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-piew.png`;
		// 		} else if (expWay === 'TPE' || expWay === '77') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-tpe.png`;
		// 		} else if (expWay === 'KJE' || expWay === '78') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-kje.png`;
		// 		} else if (expWay === 'SLE' || expWay === '79') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/ticss-sle.png`;
		// 		}

		// 	} else if (subSystem === 'wcss') {
		// 		if (expWay.indexOf('CTE') >= 0 || expWay === '71') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-cte.png`;
		// 		} else if (expWay === 'BKE' || expWay === '72') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-bke.png`;
		// 		} else if (expWay === 'ECP' || expWay === '73') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-ecp.png`;
		// 		} else if (expWay === 'AYE' || expWay === '74') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-aye.png`;
		// 		} else if (expWay === 'PIEE' || expWay === '75') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-piee.png`;
		// 		} else if (expWay === 'PIEW' || expWay === '76') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-piew.png`;
		// 		} else if (expWay === 'TPE' || expWay === '77') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-tpe.png`;
		// 		} else if (expWay === 'KJE' || expWay === '78') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-kje.png`;
		// 		} else if (expWay === 'SLE' || expWay === '79') {
		// 			imagepath = `/assets/img/Images/SS_FieldEquip/wcss-sle.png`;
		// 		}
		// 	}
		// } catch (e) {
		// }
		return imagepath;
	}
}
