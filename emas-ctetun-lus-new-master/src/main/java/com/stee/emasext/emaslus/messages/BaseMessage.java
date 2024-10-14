package com.stee.emasext.emaslus.messages;

import com.stee.emasext.emaslus.annotations.CheckSum;
import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.exceptions.LUSMessageEncodeException;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wang Yu
 * Created at 2023/2/16
 */
@Getter
@Setter
@Slf4j
public class BaseMessage implements Serializable {
    // 5 len
    private byte[] syn = { LusConstants.SYN, LusConstants.SYN,
            LusConstants.SYN, LusConstants.SYN, LusConstants.SYN };
    private byte[] soh = { LusConstants.SOH };
    // 2 len
    @CheckSum
    private byte[] length;
    // 3 len
    @CheckSum
    private byte[] address;

    // 2 len, always R, R
    @CheckSum
    private byte[] reserve = { LusConstants.reserve, LusConstants.reserve };

    @CheckSum
    private CommandCodeEnum commandCodeEnum;
    @CheckSum
    private StatusCodeEnum statusCodeEnum;

    @CheckSum
    private byte[] optionalParameter = new byte[0];

    // 2 len
    private byte[] pktSum;

    // 1 len, always 17
    private byte[] etb = { LusConstants.ETB };

    private byte[] assembledBytes;

    public BaseMessage() {
    }

    public BaseMessage(byte[] address, CommandCodeEnum commandCodeEnum, StatusCodeEnum statusCodeEnum) {
        this.address = address;
        this.commandCodeEnum = commandCodeEnum;
        this.statusCodeEnum = statusCodeEnum;
    }

    public BaseMessage(byte[] address, CommandCodeEnum commandCodeEnum,
                       StatusCodeEnum statusCodeEnum, byte[] optionalParameter) {
        this.address = address;
        this.commandCodeEnum = commandCodeEnum;
        this.statusCodeEnum = statusCodeEnum;
        this.optionalParameter = optionalParameter;
    }

    public byte[] encode() throws LUSMessageEncodeException {
        int pktLen = address.length + reserve.length +
                1 + 1 + optionalParameter.length;

        byte[] pktLenArray4Len = ByteBuffer.allocate(4).putInt(pktLen)
                .array();
        if (pktLenArray4Len[0] != 0 && pktLenArray4Len[1] != 0) {
            throw new LUSMessageEncodeException("package length overflow: length " + pktLen);
        }
        // calculate the package length
        this.length = Arrays.copyOfRange(pktLenArray4Len, 2, 4);

        // calculate the pktSum
        int pktSumInt = this.calculatePktSum();

        byte[] pktSum4Len = ByteBuffer.allocate(4).putInt(pktSumInt)
                .array();
        if (pktSum4Len[0] != 0 && pktSum4Len[1] != 0) {
            throw new LUSMessageEncodeException("package sum overflow: pktSum " + pktSumInt);
        }
        this.pktSum = Arrays.copyOfRange(pktSum4Len, 2, 4);
        int totalLength = this.syn.length + this.soh.length +
                this.length.length + address.length + reserve.length +
                1 + 1 + optionalParameter.length + this.pktSum.length + this.etb.length;

        byte[] result = new byte[totalLength];
        // assemble it into one array
        int currentIndex = 0;

        System.arraycopy(syn, 0, result, currentIndex, syn.length);
        currentIndex += syn.length;

        System.arraycopy(soh, 0, result, currentIndex, soh.length);
        currentIndex += soh.length;

        System.arraycopy(length, 0, result, currentIndex, length.length);
        currentIndex += length.length;

        System.arraycopy(address, 0, result, currentIndex, address.length);
        currentIndex += address.length;

        System.arraycopy(reserve, 0, result, currentIndex, reserve.length);
        currentIndex += reserve.length;

        result[currentIndex] = (byte) commandCodeEnum.getCode();
        currentIndex += 1;

        result[currentIndex] = (byte) statusCodeEnum.getCode();
        currentIndex += 1;

        if (optionalParameter.length != 0) {
            System.arraycopy(optionalParameter, 0, result, currentIndex, optionalParameter.length);
            currentIndex += optionalParameter.length;
        }

        System.arraycopy(pktSum, 0, result, currentIndex, pktSum.length);
        currentIndex += pktSum.length;

        System.arraycopy(etb, 0, result, currentIndex, etb.length);

        this.assembledBytes = result;

        return result;
    }

    public int calculatePktSum() {
        int result = 0;
        Field[] declaredFields = this.getClass().getDeclaredFields();
        List<Field> includeSumFiles = Arrays.stream(declaredFields)
                .filter(field -> field.isAnnotationPresent(CheckSum.class))
                .collect(Collectors.toList());
        for (Field field : includeSumFiles) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value instanceof byte[]) {
                    byte[] theArray = (byte[]) value;
                    for (byte b : theArray) {
                        result += (b & 0xff);
                    }
                } else if (value instanceof CommandCodeEnum) {
                    CommandCodeEnum codeEnum = (CommandCodeEnum) value;
                    result += codeEnum.getCode();
                } else if (value instanceof StatusCodeEnum) {
                    StatusCodeEnum statusCode = (StatusCodeEnum) value;
                    result += statusCode.getCode();
                } else {
                    log.info("this block won't happen");
                }
            } catch (Exception e) {
                log.error("calculatePktSum: ", e);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        if (this.assembledBytes == null) {
            return "null";
        }
        return LusCommonUtils.getByteHexString(this.assembledBytes);
    }
}
