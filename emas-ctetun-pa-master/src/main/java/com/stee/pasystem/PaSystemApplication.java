package com.stee.pasystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
        JmxAutoConfiguration.class
})
@EnableScheduling
@EnableRetry
public class PaSystemApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PaSystemApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
