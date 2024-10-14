package com.stee.emasext.emaslus.enums;

import com.stee.emas.common.constants.Constants;
import lombok.Getter;

/**
 * @author Wang Yu
 * Created at 2023/5/15
 */
public enum AlarmCodeEnum {
    COMMUNICATION_DOWN("ds0", "Communication Down", Constants.LUS_COMMUNICATION_DOWN_CODE),
    PHOTO_SENSOR_FAILURE("ds1", "Photo Sensor Failure", Constants.LUS_PHOTO_SENSOR_FAILURE_CODE),
    SIGN_TEMPERATURE_THRESHOLD("ds2", "Sign Temperature Threshold", Constants.LUS_SIGN_TEMPERATURE_THRESHOLD_CODE),
    PIXEL_FAILURE_ALARM("ds3", "Pixel Failure Alarm", Constants.LUS_PIXEL_FAILURE_ALARM_CODE),
    RED_PIXEL_FAILURE("ds4", "Red Pixel Failure", Constants.LUS_RED_PIXEL_FAILURE_CODE),
    GREEN_PIXEL_FAILURE("ds5", "Green Pixel Failure", Constants.LUS_GREEN_PIXEL_FAILURE_CODE),
    AMBER_PIXEL_FAILURE("ds6", "Amber Pixel Failure", Constants.LUS_AMBER_PIXEL_FAILURE_CODE),
    PIXEL_FAILURE_OF_FULL_DISPLAY("ds7", "Amber Pixel Failure", Constants.LUS_PIXEL_FAILURE_OF_FULL_DISPLAY_CODE),
    IO_TEST_FAILURE("ds8", "IO Test Failure", Constants.LUS_IO_TEST_FAILURE_CODE),
    ;

    @Getter
    private final String attrCode;
    @Getter
    private final String description;
    @Getter
    private final int alarmCode;

    AlarmCodeEnum(String attrCode, String description, int alarmCode) {
        this.attrCode = attrCode;
        this.description = description;
        this.alarmCode = alarmCode;
    }

    public static AlarmCodeEnum getAlarmCodeEnum(String attrCode) {
        for (AlarmCodeEnum alarmCodeEnum : AlarmCodeEnum.values()) {
            if (alarmCodeEnum.getAttrCode().equals(attrCode)) {
                return alarmCodeEnum;
            }
        }
        throw new IllegalArgumentException("No AttrCode [" + attrCode + "] was found");
    }
}

