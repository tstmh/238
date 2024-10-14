package com.stee.emasext.emaslus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/*
    To disable database auto configuration, add next lines to exclude:
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
 */
@SpringBootApplication(exclude = {
        JmxAutoConfiguration.class
})
@EnableJms
@EnableScheduling
@EnableAsync
public class EmasLusInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmasLusInterfaceApplication.class, args);
    }

}
