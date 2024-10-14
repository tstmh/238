package com.stee.pasystem.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * @author Wang Yu
 * crated at 2021/2/18
 */
@Configuration
@Slf4j
public class ContextConfig {

    @Value("${stee.jndi-url}")
    private String jndiUrl;

    @Bean
    public Context context() {
        log.info("Starting to create Context with weblogic JNDI...");
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        properties.put(Context.PROVIDER_URL, jndiUrl);
        try {
            return new InitialContext(properties);
        } catch (NamingException e) {
            log.error("Error while creating Context", e);
            return null;
        }
    }
}
