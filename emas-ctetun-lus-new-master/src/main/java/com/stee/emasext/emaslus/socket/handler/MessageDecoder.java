package com.stee.emasext.emaslus.socket.handler;

import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author Wang Yu
 * Created at 2022/11/14
 */
@Slf4j(topic = "socketLogger")
public class MessageDecoder extends ByteToMessageDecoder {

    private static final byte[] SYN_ARRAY = {LusConstants.SYN, LusConstants.SYN,
            LusConstants.SYN, LusConstants.SYN, LusConstants.SYN};
    private static final byte[] ETB_ARRAY = {LusConstants.ETB};

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readableBytes();
        if (length == 0) {
            return;
        }
        // example @@@@@01h00h07h01h01h00hRR02h00h0Eh0Fh17h
        byte[] arr = new byte[length];
        byteBuf.readBytes(arr);
        log.info("in decoder received: {}", LusCommonUtils.getByteHexString(arr));
        if (!Arrays.equals(Arrays.copyOfRange(arr, 0, 5), SYN_ARRAY)) {
            log.info("the message doesn't start with @@@@@, drop it");
            return;
        }
        if (!Arrays.equals(Arrays.copyOfRange(arr, length - 1, length), ETB_ARRAY)) {
            log.info("the message doesn't end with ETB, drop it");
            return;
        }
        BaseMessage baseMessage = new BaseMessage();
        baseMessage.setLength(Arrays.copyOfRange(arr, 7, 9));
        baseMessage.setAddress(Arrays.copyOfRange(arr, 9, 12));
        int commandInt = arr[13] & 0xff;
        int statusCodeInt = arr[14] & 0xff;
        baseMessage.setCommandCodeEnum(CommandCodeEnum.fromCode(commandInt));
        baseMessage.setStatusCodeEnum(StatusCodeEnum.fromCode(statusCodeInt));
        baseMessage.setPktSum(Arrays.copyOfRange(arr, length - 3, length));
        if (length - 15 - 3 > 0) {
            log.info("Message contains parameters");
            byte[] optionalParameter = Arrays.copyOfRange(arr, 15, length - 3);
            log.info("decoded parameters: {}, length: {}", Arrays.toString(optionalParameter), optionalParameter.length);
            baseMessage.setOptionalParameter(optionalParameter);
        }
        baseMessage.setAssembledBytes(arr);
        list.add(baseMessage);

    }
}
