package com.stee.emasext.emaslus.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Wang Yu
 * Created at 2022/11/11
 */
@Slf4j
public enum StatusCodeEnum {
    REQUEST(0x00, "request"),
    RESPONSE_OK(0x80, "Success"),
    SLAVE_BUSY(0x81, "Slave Busy"),
    SLAVE_IN_LOCAL_MODE(0x82, "Slave is in local mode"),
    MESSAGE_NOT_EXIST(0x83, "Message does not exist"),
    PARAMETER_WRONG(0x84, "Parameter wrong"),
    UNKNOWN_CMD_CODE(0x90, "Unknown Command Code"),
    PACKET_SUM_ERROR(0x91, "Packet Sum Error"),
    ;

    @Getter
    private final int code;
    @Getter
    private final String description;

    StatusCodeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static StatusCodeEnum fromCode(int code) {
        for (StatusCodeEnum statusCodeEnum : StatusCodeEnum.values()) {
            if (statusCodeEnum.getCode() == code) {
                return statusCodeEnum;
            }
        }
        log.warn("Status code {} not found", code);
        return null;
    }
}
