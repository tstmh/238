import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Bully, BullySubjectService } from 'src/app/share/service/bully-subject.service';
import { httpGet, wsSend, ResultVO, httpPost } from 'src/app/public/utils/webservices';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class EquipmentService {
	constructor(
		private router: Router,
		private bully: BullySubjectService,
		private httpClient: HttpClient
	) {
	}

	/**
	 * Technical Alarm
	 */
	GetTechnicalAlarmByEquipTypeAndExpwayCode() {
		return wsSend('getTechnicalAlarmByEquipTypeAndExpwayCode', { 'propertyCode': [0] }, 'AWWebService');
	}

	// 获取所有的EquipType
	getAllEquipType() {
		return wsSend('getAllEquipType', {}, 'CTETunDataService');
	}
	getTechnicalAlarmByEquipType(param) {
		return wsSend('getTechnicalAlarmByEquipType', param, 'CTETunDataService');
	}
	getEquipStatusByEquipType(param) {
		return wsSend('getEquipStatusByEquipType', param, 'CTETunDataService');
	}
	getWMSAllEquipType(): Observable<ResultVO> {
		return httpGet(this.httpClient, 'WaterMistService', 'getAllConfig');
	}
	getPlcHostByEquipTypes(param: object): Observable<ResultVO> {
		return httpGet(this.httpClient, 'WaterMistService', 'getPlcHostByEquipTypes', param);
	}
	getWMSEquipStatusByEquipType(param: object): Observable<ResultVO> {
		return httpGet(this.httpClient, 'WaterMistService', 'getEquipStatusByEquipType', param);
	}
	getWMSEquipStatusByEquipId(param: object): Observable<ResultVO> {
		return httpGet(this.httpClient, 'WaterMistService', 'getEquipStatusByEquipId', param);
	}
	getLUSAllEquipType(): Observable<ResultVO> {
		return httpGet(this.httpClient, 'NewLUSService', 'getAllEquipType');
	}
	getLUSEquipStatusByEquipType(param: object): Observable<ResultVO> {
		return httpGet(this.httpClient, 'NewLUSService', 'getEquipStatusByEquipType', param);
	}
	getLUSEquipStatusByEquipId(param: object): Observable<ResultVO> {
		return httpGet(this.httpClient, 'NewLUSService', 'getEquipStatusByEquipId', param);
	}
	GetAllVmsPictogramConfig() {
		return wsSend('getAllVmsPictogramConfig', {}, 'VMSWebService');
	}
	getVmsMsgByEquipId(param) {
		return wsSend('getVmsMsgByEquipId', param, 'VMSWebService');
	}
	getStatusByEquipIdAndStatusCode(param) {
		return wsSend('getStatusByEquipIdAndStatusCode', param, 'AWWebService');
	}
	GetAllVmsTemplate() {
		return wsSend('getAllVmsTemplate', {}, 'VMSWebService');
	}

	AW_CFELS_VMS(param) {
		return wsSend('AW_CFELS_VMS', param, 'CMHWebService');
	}

	AW_CFELS_LUS(param) {
		return wsSend('AW_CFELS_LUS', param, 'CMHTunWebService');
	}

	AW_CFELS_PMCS(param) {
		return wsSend('AW_CFELS_PMCS', param, 'CMHTunWebService');
	}
	// AW_CFELS_WMS(param: object): Observable<ResultVO> {
	// 	return httpPost(this.httpClient, 'WaterMistService', 'remoteControl', param);
	// }
	AW_CFELS_WMS(param) {
		return wsSend('waterMistControl', param, 'CMHTunWebService');
	}
	AW_CFELS_NEWLUS(str, param) {
		if (str == 'Massage') {
			return wsSend('lusRemoteControl', param, 'CMHTunWebService');
		} else if (str == 'Dimming') {
			return wsSend('lusSetupDimming', param, 'CMHTunWebService');
		} else if (str == 'Flash') {
			return wsSend('lusSetupFlashRate', param, 'CMHTunWebService');
		}		
	}
	AW_CFELS_Dimming(param) {
		return wsSend('AW_CFELS_Dimming', param, 'CMHWebService');
	}
	AW_CFELS_Reset(param) {
		return wsSend('AW_CFELS_Reset', param, 'CMHWebService');
	}
	AW_CFELS_PixelFailureBMPFile(param) {
		return wsSend('AW_CFELS_PixelFailureBMPFile', param, 'CMHWebService');
	}
	AW_CFELS_FanOpeMode(param) {
		return wsSend('AW_CFELS_FanOpeMode', param, 'CMHWebService');
	}
	AW_CFELS_VMSTimetable(param) {
		return wsSend('AW_CFELS_VMSTimetable', param, 'CMHWebService');
	}
	AW_CFELS_FlashingTime(param) {
		return wsSend('AW_CFELS_FlashingTime', param, 'CMHWebService', 1);
	}
	AW_CFELS_UploadPictogram(param) {
		return wsSend('AW_CFELS_UploadPictogram', param, 'CMHWebService');
	}
	getVmsPictogramConfigByGroupIdAndDimension(picGroupId, height, width) {
		// tslint:disable-next-line: max-line-length
		return wsSend('getVmsPictogramConfigByGroupIdAndDimension', { 'picGroupId': picGroupId, 'height': height, 'width': width }, 'VMSWebService');
	}
	getVmsPictogramConfigByGroupIdAndEquipId(picGroupId, equipId) {
		// tslint:disable-next-line: max-line-length
		return wsSend('getVmsPictogramConfigByGroupIdAndEquipId', { 'picGroupId': picGroupId, 'equipId': equipId }, 'VMSWebService');
	}
	getAllVmsPictogramConfig() {
		return wsSend('getAllVmsPictogramConfig', {}, 'VMSWebService');
	}
	getVmsLibraryByExpwayCodeAndEquipTypeAndCategory(param) {
		return wsSend('getVmsLibraryByExpwayCodeAndEquipTypeAndCategory', param, 'VMSWebService');
	}
	getVmsLibraryById(vmsLibraryId) {
		return wsSend('getVmsLibraryById', { vmsLibraryId }, 'VMSWebService');
	}
	getTechnicalAlarmByEquipId(param) {
		return wsSend('getTechnicalAlarmByEquipId', param, 'AWWebService');
	}
	covert({ equipId, equipCode, location, felsCode }) {
		if (felsCode === 'af' || felsCode === 'ax') {
			return !location ? equipId : equipId + ' - ' + location;
		} else if (equipCode === 'lus' && felsCode === 'al') {
			return !location ? equipId : equipId + ' - ' + location;
		} else if (equipCode === 'llk' && felsCode === 'al') {
			const s = equipId.spilt('');
			return equipId + ' - ' + 'Station' + s[3] + 'Port' + s[4];
		} else {
			return equipId;
		}
	}
}
