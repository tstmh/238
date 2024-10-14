package com.stee.pasystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AlarmStatusEnum {
    HEALTH(0),
    ALARM_RAISE(1),
    ;

    private final int statusCode;
}
