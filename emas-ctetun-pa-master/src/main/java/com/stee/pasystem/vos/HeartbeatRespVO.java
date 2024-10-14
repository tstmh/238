package com.stee.pasystem.vos;

import lombok.Data;

import java.io.Serializable;

@Data
public class HeartbeatRespVO implements Serializable {
    private Integer resultCode;
    private String description;
    private String responseTime;
}
