package com.stee.emasext.emaslus.controllers;

import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.exceptions.MessageTransmitException;
import com.stee.emasext.emaslus.utils.GlobalVariable;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import com.stee.emasext.emaslus.vo.TestMsgVO;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LusCommandSenderController {
    private final PropertiesConfig propertiesConfig;
    private final JmsTemplate queueTemplate;

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/listAllConnection")
    public Set<String> listAllConnection() {
        return GlobalVariable.channelMap.keySet();
    }

}
