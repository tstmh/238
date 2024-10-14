package com.stee.emasext.emaslus;

import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Wang Yu
 * Created at 2023/2/20
 */
@Slf4j
public class LusDecoderTest {
    private static final byte[] SYN_ARRAY = {LusConstants.SYN, LusConstants.SYN,
            LusConstants.SYN, LusConstants.SYN, LusConstants.SYN};
    private static final byte[] ETB_ARRAY = {LusConstants.ETB};

    @Test
    public void testDecoder() {
        String str = "40,40,40,40,40,01,00,0f,01,00,01,52,52,10,00,01,00,00,00,00,00,00,00,00,c6,17";
        String[] split = str.split(",");
        byte[] bytes = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            int aInt = Integer.parseInt(split[i], 16);
            bytes[i] = (byte) aInt;
        }
        BaseMessage baseMessage = decoderBaseFunction(bytes);
        if (baseMessage == null || baseMessage.getAssembledBytes() == null) {
            log.info("null");
            return;
        }
        log.info(LusCommonUtils.getByteHexString(baseMessage.getAssembledBytes()));
    }

    private static BaseMessage decoderBaseFunction(byte[] arr) {
        int length = arr.length;
        log.info("in decoder received: {}", LusCommonUtils.getByteHexString(arr));
        if (!Arrays.equals(Arrays.copyOfRange(arr, 0, 5), SYN_ARRAY)) {
            log.info("the message doesn't start with @@@@@, drop it");
            return null;
        }
        if (!Arrays.equals(Arrays.copyOfRange(arr, length - 1, length), ETB_ARRAY)) {
            log.info("the message doesn't end with ETB, drop it");
            return null;
        }
        BaseMessage baseMessage = new BaseMessage();
        baseMessage.setLength(Arrays.copyOfRange(arr, 7, 9));
        baseMessage.setAddress(Arrays.copyOfRange(arr, 9, 12));
        int commandInt = arr[13];
        int statusCodeInt = arr[14];
        baseMessage.setCommandCodeEnum(CommandCodeEnum.fromCode(commandInt));
        baseMessage.setStatusCodeEnum(StatusCodeEnum.fromCode(statusCodeInt));
        baseMessage.setPktSum(Arrays.copyOfRange(arr, length - 3, length));
        if (length - 15 - 3 > 0) {
            log.info("Message contains parameters");
            byte[] optionalParameter = Arrays.copyOfRange(arr, length - 15, length - 3);
            log.info("decoded parameters: {}, length: {}", Arrays.toString(optionalParameter), optionalParameter.length);
            baseMessage.setOptionalParameter(optionalParameter);
        }
        baseMessage.setAssembledBytes(arr);
        return baseMessage;
    }
}
