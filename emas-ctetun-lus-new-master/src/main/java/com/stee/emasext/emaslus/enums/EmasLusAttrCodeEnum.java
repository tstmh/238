package com.stee.emasext.emaslus.enums;

import lombok.Getter;

/**
 * @author Wang Yu
 * Created at 2023/4/28
 */
public enum EmasLusAttrCodeEnum {
    OPERATION_STATUS("ope"),
    DISPLAY_MESSAGE("stg"),
    DIMMING_MODE("stm"),
    DIMMING_LEVEL("stl"),
    DIMMING_VALUE("stv"),
    PHOTO_SENSOR("stp"),
    TEMPERATURE("stt"),
    FLASH_RATE_ON("sto"),
    FLASH_RATE_OFF("stf"),
    ;

    @Getter
    private final String attrCode;

    EmasLusAttrCodeEnum(String attrCode) {
        this.attrCode = attrCode;
    }

}
