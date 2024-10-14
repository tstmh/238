package com.stee.emasext.emaslus.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stee")
@Getter
@Setter
public class PropertiesConfig {
    // jms
    private boolean jmsEnable;
    private String jmsUsername;
    private String jmsPassword;
    private String jndiJmsFactory;
    private boolean jmsQosEnable;
    private long jmsMessageTimeToLive;
    private String jmsCmhLusQueue;
    private String jmsLusCmhQueue;
    private String jmsLusLusoldQueue;

    // jndi
    private String jndiUrl;

    // others
    private String lusInterfaceCode;
    private long lusRefreshStatusRate;
    private long lusResponseValidTime;
}
