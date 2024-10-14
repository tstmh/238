package com.stee.emas.cmh.dto.lus;

/**
 * @author Wang Yu
 * Created at 2023/5/5
 */
public class LusSetupDimmingDto extends LusRemoteControlDto {
    private Character dimmingMode;

    public Character getDimmingMode() {
        return dimmingMode;
    }

    public void setDimmingMode(Character dimmingMode) {
        this.dimmingMode = dimmingMode;
    }
}
