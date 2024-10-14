package com.stee.pasystem.vos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HealthStatusRespVO {
    @JsonProperty("resultCode")
    private Integer resultCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("responseTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String responseTime;

    @JsonProperty("responseData")
    private List<AlarmData> responseData;
}

