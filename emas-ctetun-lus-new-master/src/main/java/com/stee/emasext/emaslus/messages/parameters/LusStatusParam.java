package com.stee.emasext.emaslus.messages.parameters;

import lombok.Data;

/**
 * @author Wang Yu
 * Created at 2023/4/27
 */
@Data
public class LusStatusParam {
    private byte currentMessage;
    private byte temperature;
    private byte photoSensor;
    private byte dimmingLevel;
    private byte commStatus;
    private byte pixelFailureAlarm;
    private byte pixelFailureOfFullDisplay;
    private byte ioTestFailure;

    public LusStatusParam(byte[] bytes) {
        if (bytes.length != 8) {
            throw new IllegalArgumentException("The byte array must have a length of 8.");
        }
        this.currentMessage = (byte) (bytes[0] & 0xff);
        this.temperature = (byte) (bytes[1] & 0xff);
        this.photoSensor = (byte) (bytes[2] & 0xff);
        this.dimmingLevel = (byte) (bytes[3] & 0xff);
        this.commStatus = (byte) (bytes[4] & 0xff);
        this.pixelFailureAlarm = (byte) (bytes[5] & 0xff);
        this.pixelFailureOfFullDisplay = (byte) (bytes[6] & 0xff);
        this.ioTestFailure = (byte) (bytes[7] & 0xff);

    }
}
