package com.stee.emasext.emaslus.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Wang Yu
 * Created at 2023/5/5
 */
@RequiredArgsConstructor
public enum DimmingModeEnum {
    AUTO('A', 0, "Auto"),
    FORCE_DAY('D', 1, "Force Day"),
    FORCE_NIGHT('N', 2, "Force Night")
    ;

    @Getter
    private final char mode;
    @Getter
    private final int value;
    @Getter
    private final String description;

    public static DimmingModeEnum getDimmingModeEnumByMode(char mode) {
        for (DimmingModeEnum dimmingMode : DimmingModeEnum.values()) {
            if (dimmingMode.getMode() == mode) {
                return dimmingMode;
            }
        }
        throw new IllegalArgumentException("Invalid mode: " + mode);
    }

}
