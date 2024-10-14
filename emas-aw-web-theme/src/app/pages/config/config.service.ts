import { Injectable } from '@angular/core';
import { Bully, BullySubjectService } from 'src/app/share/service/bully-subject.service';
import { wsSend } from 'src/app/public/utils/webservices';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  /**
   * VMS Template
   */
  getAllVmsTemplate() {
    return wsSend('getAllVmsTemplate', null, 'VMSWebService');
  }
  getVmsTemplateByEquipType(data: string) {
    return wsSend('getVmsTemplateByEquipType', { 'equipType': data }, 'VMSWebService');
  }


  /**
   * Equipment
   */
  GetAllEquipConfig() {
    return wsSend('getAllEquipConfig', {}, 'CommonWebService');
  }
  GetAllCommonTypeConfig() {
    return wsSend('getAllCommonTypeConfig', {}, 'CommonWebService');
  }
  getEquipConfigBySubSystem(data) {
    return wsSend('getEquipConfigBySubSystem', { 'subSystem': data }, 'CommonWebService');
  }
  /**
   * pictogram
   */
  getAllVmsPictogramConfig() {
    return wsSend('getAllVmsPictogramConfig', {}, 'VMSWebService');
  }

  getPictogramDimensionByGroupId(picGroupId) {
    return wsSend('getPictogramDimensionByGroupId', { 'picGroupId': picGroupId }, 'VMSWebService');
  }

  getVmsPictogramConfigByGroupIdAndDimension(picGroupId, height, width) {
    // tslint:disable-next-line: max-line-length
    return wsSend('getVmsPictogramConfigByGroupIdAndDimension', { 'picGroupId': picGroupId, 'height': height, 'width': width }, 'VMSWebService');
  }

  getDimmingTimetableByEquipType(equipType) {
    return wsSend('getDimmingTimetableByEquipType', { 'equipType': equipType }, 'VMSWebService');
  }

  /**
       * fels-parameters
       */
  getAllSystemParameter() {
    return wsSend('getAllSystemParameter', {}, 'CommonWebService');
  }

  updateSystemParameter(param, value, setBy) {
    return wsSend('updateSystemParameter', { 'parameter': param, 'value': value, 'setBY': setBy }, 'CommonWebService');
  }
  GetTechnicalAlarmByEquipTypeAndExpwayCode() {
    return wsSend('getTechnicalAlarmByEquipTypeAndExpwayCode', { 'propertyCode': [0] }, 'AWWebService');
  }

  AW_CFELS_PictogramSet(param) {
    // tslint:disable-next-line: max-line-length
    return wsSend('AW_CFELS_PictogramSet', { vmsPictogramConfig: param }, 'CMHWebService');
  }
  /**
   * 对象数组去重，获取下拉框数据
   */
  getSelectList(arr, key, listName) {
    const map = new Map();
    arr.forEach((item) => {
      if (!map.has(item[key])) {
        map.set(item[key], item);
      }
    });
    // const uniqueList = [...map.values()];
    const uniqueList = Array.from(map.values());
    uniqueList.forEach((data) => {
      listName.push({ label: data[key], value: data[key] });
    });
  }
}
