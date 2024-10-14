package com.stee.emasext.emaslus.socket.handler;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.enums.AlarmCodeEnum;
import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.enums.EmasLusAttrCodeEnum;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.messages.processor.responses.*;
import com.stee.emasext.emaslus.services.primary.LusEquipConfigService;
import com.stee.emasext.emaslus.services.primary.TunnelEquipStatusService;
import com.stee.emasext.emaslus.utils.GlobalVariable;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "socketLogger")
@Service
@Scope("prototype")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ChannelHandler.Sharable
public class ClientInboundHandler extends ChannelInboundHandlerAdapter {

    private final PropertiesConfig propertiesConfig;

    private final Optional<JmsTemplate> queueTemplate;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final LusEquipConfigService lusEquipConfigService;
    private final TunnelEquipStatusService tunnelEquipStatusService;

    private final RespLusStatusProcessor respLusStatusProcessor;
    private final RespLusStatusValueProcessor respLusStatusValueProcessor;
    private final RespDimmingStatusProcessor respDimmingStatusProcessor;
    private final RespGeneralProcessor respGeneralProcessor;
    private final RespParameterProcessor respParameterProcessor;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String controllerId = ctx.channel().attr(LusConstants.ATTR_CONTROLLER_ID_KEY).get();
        log.info("channel active [{}]: remote: {}", controllerId, ctx.channel().remoteAddress());
        GlobalVariable.channelMap.put(ctx.channel().remoteAddress().toString(), ctx.channel());
        if (StringUtils.isNotEmpty(controllerId)) {
            // clear alarm
            submitTechnicalAlarmTask(controllerId, false);
            // change the controller status to online
            changeControllerStatus(controllerId,
                    LusConstants.LUS_OPE_NORMAL);
            // TODO send request of asking for attr
//            submitAskingAttrInit(ctx.channel());
        } else {
            ctx.executor().schedule(() -> {
                log.info("Cannot retrieve Controller Id from disconnected channel {}", ctx.channel());
                ctx.fireChannelActive();
            }, 2, TimeUnit.SECONDS);
        }
        super.channelActive(ctx);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof BaseMessage) {
            BaseMessage message = (BaseMessage) msg;
            log.info("receive message form server [{}] :{}", ctx.channel().remoteAddress(), message);
            if (message.getCommandCodeEnum() == null) {
                log.warn("the message or message command doesn't declare");
                return;
            }
            String controllerId = ctx.channel().attr(LusConstants.ATTR_CONTROLLER_ID_KEY).get();
            log.info("Socket: {} receive {} command", controllerId, message.getCommandCodeEnum());
            // handle the received message
            switch (message.getCommandCodeEnum()) {
                case RESP_LUS_STATUS:
                    respLusStatusProcessor.process(controllerId, message);
                    break;
                case RESP_LUS_STATUS_VALUE:
                    respLusStatusValueProcessor.process(controllerId, message);
                    break;
                case RESP_DIMMING_STATUS:
                    respDimmingStatusProcessor.process(controllerId, message);
                    break;
                case RESP_PARAMETER:
                    respParameterProcessor.process(controllerId, message);
                    break;
                case RESP_DISPLAY_MESSAGE:
                case RESP_SETUP_DIMMING_MODE:
                    respGeneralProcessor.process(controllerId, message);
                    break;
                default:
                    log.info("no processor for code {}", message.getCommandCodeEnum().getCode());
                    break;
            }
        } else {
            log.warn("the message handler cannot identify the decoded message, drop it");
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        try {
            log.info("channel registered: {}", ctx.channel());
        } catch (Exception e) {
            log.error("channelRegistered", e);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Optional<Channel> channel = Optional.ofNullable(ctx.channel());
        channel.ifPresent(value -> {
            if (value.remoteAddress() != null) {
                log.info("channel unregistered: {}", value.remoteAddress());
            }
        });
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive: {}", ctx.channel().remoteAddress().toString());
        String controllerId = ctx.channel().attr(LusConstants.ATTR_CONTROLLER_ID_KEY).get();
        GlobalVariable.channelMap.remove(ctx.channel().remoteAddress().toString());
        // generate alarm: communication down
        submitTechnicalAlarmTask(controllerId, true);
        // change the controller ope status to offline
        changeControllerStatus(ctx.channel().attr(LusConstants.ATTR_CONTROLLER_ID_KEY).get(),
                LusConstants.LUS_OPE_ABNORMAL);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel exception on {} ",
                ctx.channel().remoteAddress().toString(), cause);
    }

    private void changeControllerStatus(String controllerId, int value) {
        if (controllerId == null) {
            return;
        }
        TunnelEquipStatus tunnelEquipStatus = TunnelEquipStatus
                .builder()
                .equipId(controllerId)
                .attrCode(EmasLusAttrCodeEnum.OPERATION_STATUS.getAttrCode())
                .attrValue(value)
                .build();
        tunnelEquipStatusService.saveStatus(tunnelEquipStatus);
    }

    private void submitTechnicalAlarmTask(String controllerId, boolean isRaised) {
        if (queueTemplate.isPresent()) {
            JmsTemplate jmsTemplate = queueTemplate.get();
            // submit a task
            threadPoolTaskExecutor.submit(() -> {
                TechAlarmDtoList listDto = new TechAlarmDtoList();
                List<TechnicalAlarmDto> list = new ArrayList<>();

                Optional<LusEquipConfig> equipOption = lusEquipConfigService.getEquipById(controllerId);
                if (isRaised) {
                    log.warn("Task Executor - channelInactive: controller: {} lost connection", controllerId);
                }
                if (equipOption.isPresent()) {
                    LusEquipConfig controller = equipOption.get();
                    TechnicalAlarmDto dto = LusCommonUtils.createAlarmDto(controller,
                            AlarmCodeEnum.COMMUNICATION_DOWN,
                            isRaised ? Constants.ALARM_RAISED : Constants.ALARM_CLEARED);
                    list.add(dto);
                    listDto.setDtoList(list);
                    MessageTransmitUtils.sendJms(jmsTemplate, propertiesConfig.getJmsLusCmhQueue(),
                            listDto, MessageConstants.TECH_ALARM_ID);
                } else {
                    log.warn("Task Executor - submitTechnicalAlarmTask: cannot found controller: {}", controllerId);
                }
            });
        }

    }

    /*private void submitAskingAttrInit(Channel channel) {
        threadPoolTaskExecutor.submit(() -> {
            MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.REQ_DIMMING_STATUS, null, channel);
            MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.REQ_PARAMETER, LusConstants.PARAMETER_ID_REQUEST, channel);
        });
    }*/

}
