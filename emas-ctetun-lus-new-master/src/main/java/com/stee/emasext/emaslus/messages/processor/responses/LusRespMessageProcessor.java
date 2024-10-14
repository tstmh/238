package com.stee.emasext.emaslus.messages.processor.responses;

import com.stee.emasext.emaslus.messages.BaseMessage;

/**
 * @author Wang Yu
 * Created at 2023/5/4
 */
public interface LusRespMessageProcessor {
    void process(String controllerId, BaseMessage baseMessage);
}
