package com.stee.emasext.emaslus.tasks;

import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.services.primary.TunnelEquipStatusService;
import com.stee.emasext.emaslus.socket.handler.ClientInboundHandler;
import com.stee.emasext.emaslus.socket.handler.ClientOutboundHandler;
import com.stee.emasext.emaslus.socket.handler.ClientReconnectHandler;
import com.stee.emasext.emaslus.socket.handler.MessageDecoder;
import com.stee.emasext.emaslus.utils.GlobalVariable;
import com.stee.emasext.emaslus.utils.LusConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * @author Wang Yu
 * Created at 2022/11/10
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NioClientCreationTask implements ApplicationRunner {

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final PropertiesConfig propertiesConfig;
    private final LusEquipConfigRepository lusEquipConfigRepository;
    private final ClientInboundHandler clientInboundHandler;
    private final TunnelEquipStatusService tunnelEquipStatusService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<LusEquipConfig> controllers = lusEquipConfigRepository.listAllController();
        if (controllers.isEmpty()) {
            log.warn("Controller size is 0");
            return;
        }
        for (LusEquipConfig controller : controllers) {
            threadPoolTaskExecutor.submit(() -> {
                createConnection(controller.getEquipId(), controller.getIpAddress(), controller.getPort());
            });
        }
    }

    private void createConnection(String controllerId, String host, int port) {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            log.info("connecting to server {}:{} ", host, port);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            .addLast("Message Decoder", new MessageDecoder())
                            .addLast("Reconnect Resolver", new ClientReconnectHandler(host, port, bootstrap, controllerId))
                            .addLast("Inbound Handler", clientInboundHandler)
                            .addLast("Outbound Handler", new ClientOutboundHandler())
                    ;
                }
            });
            ChannelFuture channelFuture = bootstrap
                    .connect(host, port)
                    .awaitUninterruptibly();
            Channel channel = channelFuture.channel();
            // put controller Id to attribute map
            channel.attr(LusConstants.ATTR_CONTROLLER_ID_KEY).set(controllerId);
        } catch (Exception e) {
            log.error("Error occurs while trying to boot socket connection: {}:{}", host, port);
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("shutting down all socket channels");
        GlobalVariable.channelMap.forEach((k, v) -> {
            v.eventLoop().shutdownGracefully();
        });
    }

}
