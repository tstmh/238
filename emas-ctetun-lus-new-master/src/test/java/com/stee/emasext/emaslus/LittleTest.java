package com.stee.emasext.emaslus;

import com.stee.emas.common.tunnel.TunnelRemoteDto;
import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.enums.DisplayMessageEnum;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.util.text.AES256TextEncryptor;
import org.junit.jupiter.api.Test;

import java.io.ObjectStreamClass;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Wang Yu
 * Created at 2022/11/11
 */
@Slf4j
public class LittleTest {
    @Test
    public void testLeftPad() {
        String len = "EF";
        String res = StringUtils.leftPad(len, 4, '0');
        System.out.println(res);
        String len1 = StringUtils.substring(res, 0, 2);
        String len2 = StringUtils.substring(res, 2, 4);
        log.info("{}, {}", len1, len2);
    }

    @Test
    public void generateLusStatus() {
        byte[] bytes = new byte[8 * 8];

        byte[] others = { (byte)0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

        int index = 0;
        while (index < bytes.length) {
            int copyLength = Math.min(bytes.length - index, others.length);
            System.arraycopy(others, 0, bytes, index, copyLength);
            index += copyLength;
        }

        log.info("byte is {}", Arrays.toString(bytes));

        byte[] lus01 = {(byte)DisplayMessageEnum.ALL_OFF.getCode(),
        '1','1', 0x0C, '1', '1', 0x00, '1'};
        System.arraycopy(lus01, 0, bytes, 0, 8);
        BaseMessage baseMessage = new BaseMessage(LusConstants.ADDR_BYTE,
                CommandCodeEnum.RESP_LUS_STATUS, StatusCodeEnum.RESPONSE_OK,
                bytes);
        baseMessage.encode();
        log.info(baseMessage.toString());
    }

    @Test
    public void generateLusValueMessage() {
        byte[] bytes = new byte[8];
        Arrays.fill(bytes, (byte) 0x01);

        bytes[5] = 0x32;
        BaseMessage baseMessage = new BaseMessage(LusConstants.ADDR_BYTE,
                CommandCodeEnum.RESP_LUS_STATUS_VALUE, StatusCodeEnum.RESPONSE_OK,
                bytes);
        baseMessage.encode();
        log.info(baseMessage.toString());
    }

    @Test
    public void testPutIntToByteArray() {
        int a = 0x7fff;
        byte[] array = ByteBuffer.allocate(4).putInt(a)
                .array();
        log.info(Arrays.toString(array));
        String byteHexString = LusCommonUtils.getByteHexString(array);
        log.info(byteHexString);
    }

    @Test
    public void testSerializeID() {
        long serialVersionUID = ObjectStreamClass.lookup(TunnelRemoteDto.class).getSerialVersionUID();
        System.out.println(serialVersionUID);
    }

    @Test
    public void generateReportDimmingStatus() {
        byte[] bytes = new byte[26];
        Arrays.fill(bytes, (byte) 0x01);

        bytes[12] = 'D';
        BaseMessage baseMessage = new BaseMessage(LusConstants.ADDR_BYTE,
                CommandCodeEnum.RESP_DIMMING_STATUS, StatusCodeEnum.RESPONSE_OK,
                bytes);
        baseMessage.encode();
        log.info(baseMessage.toString());
    }

    @Test
    public void testCalculateLen() {
        byte[] pktLenArray4Len = ByteBuffer.allocate(4).putInt(15)
                .array();
        byte[] bytes = Arrays.copyOfRange(pktLenArray4Len, 2, 4);
        System.out.println(Arrays.toString(bytes));
    }

    @Test
    public void testLog() {
        log.info("{}", LusConstants.LUS_ALARM_MAPPING);
    }

    @Test
    public void encryptPassword() {
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("r78Y2$G#J^@oP9!d");
/*        config.setAlgorithm("PBEWithMD5AndDES");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");*/
        encryptor.setPassword("r78Y2$G#J^@oP9!d");
        String encrypt = encryptor.encrypt("P@ssword");
        System.out.println(encrypt);
    }

    @Test
    public void decryptPassword() {
        /*StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("r78Y2$G#J^@oP9!d");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        String decrypt = encryptor.decrypt("ZtqlpkU5/Q0JoOTc9+yaUrOSvn4LWsF2");
        System.out.println(decrypt);*/
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword("r78Y2$G#J^@oP9!d");
        String decrypt = encryptor.decrypt("veiNl0XZ7761liWr5wW50xkcJ8gCQDMUiC+F8qq0RM25E3xVfjlMrRKufJRJKwXG");
        System.out.println(decrypt);
    }

}
