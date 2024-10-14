package com.stee.emasext.emaslus.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Wang Yu
 * Created at 2022/11/11
 */
@Slf4j
public enum CommandCodeEnum {
    // common command
    RESET(0x02),
    REQ_FIRMWARE_VERSION(0x03),
    RESP_FIRMWARE_VERSION(0x83),
    SETUP_DIMMING(0x04),
    REQ_DIMMING_STATUS(0x05),
    RESP_DIMMING_STATUS(0x85),
    REQ_PARAMETER(0x07),
    RESP_PARAMETER(0x87),
    SETUP_LOCAL_CONTROLLER_PASSWORD(0x08),
    UPDATE_LOCAL_CONTROLLER_FIRMWARE(0x09),
    REQ_PIXEL_FAILURE_DETECT(0x0B),
    // LUS command
    DISPLAY_MESSAGE(0x10),
    REQ_LUS_STATUS(0x11),
    RESP_LUS_STATUS(0x91),
    REQ_LUS_STATUS_VALUE(0x12),
    RESP_LUS_STATUS_VALUE(0x92),
    // General response
    RESP_DISPLAY_MESSAGE(0x90),
    RESP_SETUP_DIMMING_MODE(0x84),

    RESP_NON_ACK(0xFF),
    ;
    @Getter
    private final int code;

    CommandCodeEnum(int code) {
        this.code = code;
    }

    public static CommandCodeEnum fromCode(int code) {
        for (CommandCodeEnum commandCodeEnum : CommandCodeEnum.values()) {
            if (commandCodeEnum.getCode() == code) {
                return commandCodeEnum;
            }
        }
        log.warn("command code {} not found", code);
        return null;
    }
}
