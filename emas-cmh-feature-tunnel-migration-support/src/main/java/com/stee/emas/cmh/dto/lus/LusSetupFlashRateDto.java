package com.stee.emas.cmh.dto.lus;

/**
 * @author Wang Yu
 * Created at 2023/5/9
 */
public class LusSetupFlashRateDto extends LusRemoteControlDto {
    private int flashOn;
    private int flashOff;

    public int getFlashOn() {
        return flashOn;
    }

    public void setFlashOn(int flashOn) {
        this.flashOn = flashOn;
    }

    public int getFlashOff() {
        return flashOff;
    }

    public void setFlashOff(int flashOff) {
        this.flashOff = flashOff;
    }
}
