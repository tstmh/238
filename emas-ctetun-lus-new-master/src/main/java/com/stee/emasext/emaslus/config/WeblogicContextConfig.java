package com.stee.emasext.emaslus.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * @author Wang Yu
 * crated at 2022/11/10
 */
@Configuration
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ConditionalOnProperty(name = "stee.jms-enable", havingValue = "true", matchIfMissing = true)
public class WeblogicContextConfig {

    private final PropertiesConfig propertiesConfig;

    @Bean
    public Context context() {
        log.info("Starting to create Context with weblogic JNDI...");
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        properties.put(Context.PROVIDER_URL, propertiesConfig.getJndiUrl());
        try {
            return new InitialContext(properties);
        } catch (NamingException e) {
            log.error("Error while creating Context", e);
            return null;
        }
    }

}
