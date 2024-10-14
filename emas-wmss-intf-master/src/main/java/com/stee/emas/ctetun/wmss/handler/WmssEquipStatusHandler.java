package com.stee.emas.ctetun.wmss.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.ctetun.wmss.dto.WmssBufferDto;
import com.stee.emas.ctetun.wmss.dto.WmssEquipConfigDto;
import com.stee.emas.ctetun.wmss.dto.WmssEquipStatusData;
import com.stee.emas.ctetun.wmss.dto.WmssEquipStatusDto;

@Service
public class WmssEquipStatusHandler {

	private final static Logger logger = LoggerFactory.getLogger(WmssEquipStatusHandler.class);

	@Value("${restEquipStatusUrl}")
	private String restEquipStatusUrl;

	@Autowired
	WmssRestServiceHandler wmcsRestServiceHandler;
	
	@Autowired
	WmssBufferDto wmssBufferDto;
	
	@Autowired
	WmssDataProcess wmssDataProcess;

	public void getAllEquipStatus() {
		logger.info("Calling getAllEquipStatus .....");
		String response = wmcsRestServiceHandler.callRestService(restEquipStatusUrl);
		//logger.info("Response ....." + response);
		parseEquipmentStatusResponse(response);
	}
	
	public void parseEquipmentStatusResponse(String pResponse) {
		try {
			
			Integer opeStatus = null;
			Integer alarmValue = null;
			Gson gson = new Gson();
			WmssEquipStatusData wmssEquipStatusData = gson.fromJson(pResponse, WmssEquipStatusData.class);	
			if(wmssEquipStatusData == null) {
				return;
			}
			for (WmssEquipStatusDto lWmssEquipStatusDto : wmssEquipStatusData.getEquipStatus()) {

				Integer status = Integer.parseInt(lWmssEquipStatusDto.getEquipStatus());
				WmssEquipConfigDto lWmssEquipConfigDto = wmssBufferDto.getWmssEquipConfigMap().get(lWmssEquipStatusDto.getEquipId());
				if(lWmssEquipConfigDto == null) {
					continue;
				}
				if(lWmssEquipConfigDto.getEquipType().equalsIgnoreCase(Constants.WMC_EQUIP_TYPE)) {
					opeStatus = wmssBufferDto.getWmssEquipStatusMap().get(lWmssEquipStatusDto.getEquipId(), Constants.OPE_STATUS);
					logger.info("lWmssEquipStatusDto Equip Id::: " + lWmssEquipStatusDto.getEquipId()+ ", status: " +  status + ", ope status: " + opeStatus);
					if(opeStatus == null) {
						continue;
					}
					if(status == 1)
						status = status + 1;
					if (status!= opeStatus) {
						logger.info("***** Equipment Status Changed *****");
						logger.info("EquipId :: " + lWmssEquipStatusDto.getEquipId());
						logger.info("IpAddress :: " + lWmssEquipStatusDto.getIpAddress());
						logger.info("Equip Status :: " + lWmssEquipStatusDto.getEquipStatus());
						if(opeStatus == 0) {
							opeStatus = 2;
						}
						else if(opeStatus == 2) {
							opeStatus = 0;
						}
						wmssDataProcess.handleEquipStatus(lWmssEquipConfigDto, lWmssEquipStatusDto, Constants.OPE_STATUS, opeStatus);
					}
				}
				else {
					alarmValue = wmssBufferDto.getWmssEquipStatusMap().get(lWmssEquipStatusDto.getEquipId(), Constants.WMSS_ALARM);
					logger.info("lWmssEquipStatusDto Equip Id::: " + lWmssEquipStatusDto.getEquipId()+ ", dsa Alarm Value: " +  alarmValue);
					if(alarmValue == null) {
						continue;
					}
					if (!lWmssEquipStatusDto.getEquipStatus().equalsIgnoreCase(alarmValue.toString())) {
						logger.info("***** Equipment Status Changed *****");
						logger.info("EquipId :: " + lWmssEquipStatusDto.getEquipId());
						logger.info("IpAddress :: " + lWmssEquipStatusDto.getIpAddress());
						logger.info("Equip Status :: " + lWmssEquipStatusDto.getEquipStatus());
						if(alarmValue == 1) {
							alarmValue = 0;
						}
						else if(alarmValue == 0) {
							alarmValue = 1;
						}
						wmssDataProcess.handleEquipStatus(lWmssEquipConfigDto, lWmssEquipStatusDto, Constants.WMSS_ALARM, alarmValue);
					}
				}
				logger.info("**********************************************************");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in processing Equipment Alarms.....", e);
		}
	}
}