package com.stee.emas.cmh.integration.tunnel;

import com.stee.emas.cmh.integration.Group1FELSMessageHandler;
import com.stee.emas.common.tunnel.ItptTunnelRemoteControlDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("wmcsItpt2TunnelHandler")
public class WmcsItpt2TunnelHandler implements Itpt2TunnelHandler {
    private final Group1FELSMessageHandler group1FELSMessageHandler;

    @Autowired
    public WmcsItpt2TunnelHandler(Group1FELSMessageHandler group1FELSMessageHandler) {
        this.group1FELSMessageHandler = group1FELSMessageHandler;
    }

    @Override
    public void handleRemoteControl(ItptTunnelRemoteControlDto remoteControlDto) {
        group1FELSMessageHandler.handleWmcsItptRemoteControl(remoteControlDto);
    }

}
