package com.stee.emasext.emaslus.socket.handler;

import com.stee.emasext.emaslus.utils.LusConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Wang Yu
 * Created at 2023/6/6
 */
@Slf4j(topic = "socketLogger")
@AllArgsConstructor
public class ClientReconnectHandler extends ChannelInboundHandlerAdapter {

    private String host;
    private int port;
    private Bootstrap bootstrap;
    private String controllerId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Reconnection resolver: channel active [{}]: {}", ctx.channel(), ctx.channel().remoteAddress());
        ctx.channel().attr(LusConstants.ATTR_CONTROLLER_ID_KEY).set(controllerId);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // try to reconnect LUS
        if (!ctx.channel().isActive()) {
            log.info("Reconnection resolver: Scheduling reconnect ...");
            reconnect();
            log.info("Reconnection resolver: Scheduling reconnect done");
        }
        super.channelInactive(ctx);
    }

    private void reconnect() {
        EventLoopGroup group = bootstrap.config().group();
        log.info("reconnecting to server ... {}:{}", host, port);
        bootstrap.connect(host, port).addListener(
                (ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        log.info("reconnected successfully");
                    } else {
                        group.schedule(() -> {
                            log.info("Attempt to reconnect LUS [{}] on {}:{} ...", controllerId, host, port);
                            try {
                                reconnect();
                            } catch (Exception e) {
                                log.error("Error occurs on reconnect: ", e);
                            }
                        }, 10, TimeUnit.SECONDS);
                    }
                }
        );

    }
}
