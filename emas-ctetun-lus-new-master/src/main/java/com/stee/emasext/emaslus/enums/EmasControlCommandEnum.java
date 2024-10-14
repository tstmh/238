package com.stee.emasext.emaslus.enums;

import lombok.Getter;

/**
 * @author Wang Yu
 * Created at 2023/4/26
 */
public enum EmasControlCommandEnum {
    DISPLAY_MESSAGE("cdd", CommandCodeEnum.DISPLAY_MESSAGE.getCode()),
    SET_DIMMING_LEVEL("cdm", CommandCodeEnum.SETUP_DIMMING.getCode()),
    SET_FLASH_RATE("cdf", CommandCodeEnum.REQ_PARAMETER.getCode()),
    SET_PIXEL_FAILURE_DEFECT("cdp", CommandCodeEnum.REQ_PIXEL_FAILURE_DETECT.getCode()),

    ;
    @Getter
    private final String emasAttr;
    @Getter
    private final int lusCommand;

    EmasControlCommandEnum(String emasAttr, int lusCommand) {
        this.emasAttr = emasAttr;
        this.lusCommand = lusCommand;
    }

    public static EmasControlCommandEnum getByAttrCode(String emasAttr) {
        for (EmasControlCommandEnum emasCode : EmasControlCommandEnum.values()) {
            if (emasCode.getEmasAttr().equals(emasAttr)) {
                return emasCode;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + emasAttr);
    }
}
