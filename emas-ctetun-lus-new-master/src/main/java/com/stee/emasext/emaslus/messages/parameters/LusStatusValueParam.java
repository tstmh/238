package com.stee.emasext.emaslus.messages.parameters;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Wang Yu
 * Created at 2023/4/28
 */
@Data
public class LusStatusValueParam implements Serializable {
    private byte redPixelFailure;
    private byte greenPixelFailure;
    private byte amberPixelFailure;
    private byte temperature;
    private byte photoSensorValue;
    private byte currentDimmingValue;
    private byte reserved1;
    private byte reserved2;

    public LusStatusValueParam(byte[] values) {
        if (values.length != 8) {
            throw new IllegalArgumentException("The byte array must have a length of 8.");
        }
        redPixelFailure = (byte) (values[0] & 0xff);
        greenPixelFailure = (byte) (values[1] & 0xff);
        amberPixelFailure = (byte) (values[2] & 0xff);
        temperature = (byte) (values[3] & 0xff);
        photoSensorValue = (byte) (values[4] & 0xff);
        currentDimmingValue = (byte) (values[5] & 0xff);
        reserved1 = (byte) (values[6] & 0xff);
        reserved2 = (byte) (values[7] & 0xff);
    }
}
