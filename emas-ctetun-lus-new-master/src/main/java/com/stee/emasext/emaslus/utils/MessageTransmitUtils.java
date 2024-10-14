package com.stee.emasext.emaslus.utils;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.exceptions.MessageTransmitException;
import com.stee.emasext.emaslus.jms.LusJmsMessage;
import com.stee.emasext.emaslus.messages.BaseMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;

/**
 * @author Wang Yu
 * Created at 2022/12/21
 */
@Slf4j
public class MessageTransmitUtils {

    private static final String LUS_OLD_QUEUE = "jms/lus_lusold_queue";
    private static final String LUS_CMH_QUEUE = "jms/lus_cmh_queue";

    private MessageTransmitUtils() {
    }

    public static void sendMessageToSocket(CommandCodeEnum commandCodeEnum, String controllerId) {
        sendMessageToSocket(commandCodeEnum, null, controllerId);
    }

    public static void sendMessageToSocket(CommandCodeEnum commandCodeEnum, byte[] optionalParam, String controllerId) {
        String ipAddr = GlobalVariable.ControllerIdIpMap.get(controllerId);
        if (StringUtils.isEmpty(ipAddr)) {
            log.error("The Controller Id didn't include in DB when Application has been started: {}", controllerId);
            throw new IllegalArgumentException("The Controller Id is not correct or is doesn't include in DB: " + controllerId);
        }
        Channel channel = GlobalVariable.channelMap.get(ipAddr);
        if (channel == null) {
            throw new MessageTransmitException("ip address [" + ipAddr +  "] is down or cannot communicate");
        }
        sendMessageToSocketByLc(commandCodeEnum, optionalParam, channel, getLcId(controllerId));
    }

    public static void sendMessageToSocketByLc(CommandCodeEnum commandCodeEnum, byte[] optionalParam,
                                               Channel channel, int lcId)
            throws MessageTransmitException {
        byte[] address = lcId == 1 ? LusConstants.ADDR_BYTE : LusConstants.ADDR_BYTE_2;
        BaseMessage baseMessage = new BaseMessage(address, commandCodeEnum, StatusCodeEnum.REQUEST);
        if (optionalParam != null && optionalParam.length > 0) {
            baseMessage.setOptionalParameter(optionalParam);
        }
        byte[] encodedBytes = baseMessage.encode();
        channel.writeAndFlush(Unpooled.wrappedBuffer(encodedBytes));
    }

    public static void broadcastMessageToSocket(CommandCodeEnum commandCodeEnum, BaseMessage baseMessage) throws MessageTransmitException {
        if (GlobalVariable.channelMap.isEmpty()) {
            throw new MessageTransmitException("no active channel");
        } else {
            if (commandCodeEnum == null) {
                throw new MessageTransmitException("command not supported");
            }
            GlobalVariable.channelMap.forEach((k, v) ->
                {
                    changeLCAddressFromIp(baseMessage, k);
                    byte[] encodedBytes = baseMessage.encode();
                    v.writeAndFlush(Unpooled.wrappedBuffer(encodedBytes));
                });
        }
    }

    public static void broadcastMessageToSocket(CommandCodeEnum commandCodeEnum, byte[] optionalParam) throws MessageTransmitException {
        BaseMessage baseMessage = new BaseMessage(LusConstants.ADDR_BYTE, commandCodeEnum, StatusCodeEnum.REQUEST);
        if (optionalParam != null && optionalParam.length > 0) {
            baseMessage.setOptionalParameter(optionalParam);
        }
        broadcastMessageToSocket(commandCodeEnum, baseMessage);
    }

    public static void broadcastMessageToSocket(CommandCodeEnum commandCodeEnum) throws MessageTransmitException {
        broadcastMessageToSocket(commandCodeEnum, new byte[0]);
    }

    public static void broadcastMessageToSocket(int commandCode, byte[] optionalParam) throws MessageTransmitException {
        CommandCodeEnum commandCodeEnum = CommandCodeEnum.fromCode(commandCode);
        broadcastMessageToSocket(commandCodeEnum, optionalParam);
    }

    public static <T extends Serializable> void sendJms(JmsTemplate jmsTemplate,
                                                        String destination, T msg, String id) {
        LusJmsMessage<T> lusJmsMessage = new LusJmsMessage<>(msg, id);
        jmsTemplate.convertAndSend(destination, lusJmsMessage);
        log.info("Sent JMS msg {} to {}", msg, destination);
        // forward to Lus Old
        if (StringUtils.equals(destination, LUS_CMH_QUEUE)) {
            if (!StringUtils.equals(id, MessageConstants.CMD_RESP_ID)) {
                forwardToLusOld(jmsTemplate, lusJmsMessage);
            }
        }
    }

    public static <T extends Serializable> void forwardToLusOld(JmsTemplate jmsTemplate, LusJmsMessage<T> lusJmsMessage) {
        jmsTemplate.convertAndSend(LUS_OLD_QUEUE, lusJmsMessage);
        log.info("Forwarded JMS msg {} to {}", lusJmsMessage, LUS_OLD_QUEUE);
    }

    public static void changeLCAddressFromIp(BaseMessage baseMessage, String ip) {
        String controllerId = GlobalVariable.IpAndControllerIdMap.get(ip);
        changeLCAddressFromId(baseMessage, controllerId);
    }

    public static void changeLCAddressFromId(BaseMessage baseMessage, String controllerId) {
        int lcId = getLcId(controllerId);
        if (lcId == 2) {
            baseMessage.setAddress(LusConstants.ADDR_BYTE_2);
        }
    }

    public static int getLcId(String controllerId) {
        if (controllerId.contains("_")) {
            // Split the string by "_" and get the last part
            String[] parts = controllerId.split("_");
            String lastPart = parts[parts.length - 1];
            // Check if the last part is either "1" or "2"
            if ("2".equals(lastPart)) {
                return 2;
            }
        }
        return 1;
    }

}
