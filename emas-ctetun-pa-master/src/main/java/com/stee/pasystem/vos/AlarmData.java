package com.stee.pasystem.vos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlarmData {
    @JsonProperty("locationID")
    private String locationId;

    @JsonProperty("zone")
    private String zone;

    @JsonProperty("systemID")
    private int systemId;

    @JsonProperty("deviceID")
    private String deviceId;

    @JsonProperty("alarmCode")
    private int alarmCode;

    @JsonProperty("alarmStatus")
    private int alarmStatus;

    @JsonProperty("alarmDatetime")
    private String alarmDatetime;

    @JsonProperty("information")
    private String information;

}
