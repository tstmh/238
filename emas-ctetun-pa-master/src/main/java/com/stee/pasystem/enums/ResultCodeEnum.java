package com.stee.pasystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCodeEnum {
    SUCCESS(0),
    INVALID_PARAMETER_TYPE(1),
    INVALID_PARAMETER_RANGE(2),
    INVALID_FUNCTION_CALL(3),
    SYSTEM_FAILURE(4),
    INVALID_REFERENCE_NUMBER(5),
    ;

    private final int code;
}
