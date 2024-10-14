package com.stee.emasext.emaslus.utils;

import com.stee.emas.common.constants.Constants;
import io.netty.util.AttributeKey;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LusConstants {
    private LusConstants() {}

    public static final byte SYN = '@';
    public static final byte SOH = 0x01;
    public static final byte ADDR_LUS = 0x01;
    public static final byte ADDR_2 = 0x00;
    public static final byte ADDR_3 = 0x01;
    public static final byte ADDR_3_2 = 0x02;
    public static final byte[] ADDR_BYTE = {ADDR_LUS, ADDR_2, ADDR_3};
    public static final byte[] ADDR_BYTE_2 = {ADDR_LUS, ADDR_2, ADDR_3_2};
    public static final byte[] PARAMETER_ID_SETUP = {(byte) 0x01};
    public static final byte[] PARAMETER_ID_REQUEST = {(byte) 0x02};

    public static final byte reserve = 'R';
    public static final byte ETB = 0x17;

    public static final String LUS_FELS_CODE = "lus";
    public static final String SPACE = " ";
    public static final String SYSTEM = "system";

    public static final int LUS_RUNNING_STATUS = 2;

    public static final int LUS_OPE_ABNORMAL = 0;
    public static final int LUS_OPE_NORMAL = 2;

    public static final int LUS_EQUIP_ABNORMAL = (int) '0';
    public static final int LUS_EQUIP_NORMAL = (int) '1';

    public static final String DATE_PATTERN = "dd/MM/yyyy";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_FORMAT = DATE_PATTERN + SPACE + TIME_PATTERN;
    public static final DateTimeFormatter DATE_TIME_FORMATTER =  DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final AttributeKey<String> ATTR_CONTROLLER_ID_KEY = AttributeKey.valueOf("controllerId");

    public static final Map<Integer, Integer> LUS_ALARM_MAPPING = new HashMap<Integer, Integer>() {
        {
            put(LusConstants.LUS_EQUIP_ABNORMAL, Constants.ALARM_RAISED);
            put(LusConstants.LUS_EQUIP_NORMAL, Constants.ALARM_CLEARED);
        }
    };

}
