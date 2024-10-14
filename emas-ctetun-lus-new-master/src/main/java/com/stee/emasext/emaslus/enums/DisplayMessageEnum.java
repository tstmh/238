package com.stee.emasext.emaslus.enums;

import lombok.Getter;

/**
 * @author Wang Yu
 * Created at 2023/4/26
 */
public enum DisplayMessageEnum {
    ALL_OFF(0x00),
    STEADY_RED_CROSS(0x01),
    STEADY_AMBER_CROSS(0x02),
    STEADY_GREEN_ARROW_DOWN(0x03),
    STEADY_AMBER_DOWN_LEFT_ARROW(0x04),
    STEADY_AMBER_DOWN_RIGHT_ARROW(0x05),
    FLASHING_RED_CROSS(0x81),
    FLASHING_AMBER_CROSS(0x82),
    FLASHING_GREEN_ARROW_DOWN(0x83),
    
    NOT_AVAILABLE(0xff),
    ;

    @Getter
    private final int code;

    DisplayMessageEnum(int code) {
        this.code = code;
    }

    public static DisplayMessageEnum getByCode(int code) {
        for (DisplayMessageEnum displayMessage : DisplayMessageEnum.values()) {
            if (displayMessage.getCode() == code) {
                return displayMessage;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

}
