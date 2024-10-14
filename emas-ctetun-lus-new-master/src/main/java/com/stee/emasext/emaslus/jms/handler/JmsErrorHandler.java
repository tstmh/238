package com.stee.emasext.emaslus.jms.handler;

import com.stee.emasext.emaslus.config.WeblogicContextConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

/**
 * @author Wang Yu
 * crated at 2021/11/10
 */
@Service
@Slf4j
@ConditionalOnBean(WeblogicContextConfig.class)
public class JmsErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable throwable) {
        log.error("error while listening JMS messages:", throwable);
    }
}
