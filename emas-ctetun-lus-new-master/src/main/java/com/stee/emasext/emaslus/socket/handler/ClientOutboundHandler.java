package com.stee.emasext.emaslus.socket.handler;

import com.stee.emasext.emaslus.utils.LusCommonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "socketLogger")
public class ClientOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        ByteBuf copy = byteBuf.copy();
        super.write(ctx, msg, promise);
        byte[] bytes = ByteBufUtil.getBytes(copy);
        String byteHexString = LusCommonUtils.getByteHexString(bytes);
        log.info("client wrote msg to channel {} : {}, the size is {}",
                ctx.channel().remoteAddress().toString(), byteHexString, bytes.length);
    }


}
