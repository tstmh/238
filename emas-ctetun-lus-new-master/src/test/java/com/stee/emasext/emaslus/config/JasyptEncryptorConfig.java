package com.stee.emasext.emaslus.config;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"stee.jms-enable=false"}
)
public class JasyptEncryptorConfig {
    @Autowired
    StringEncryptor jasyptStringEncryptor;
    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testEncrypt() {
        String encrypt = jasyptStringEncryptor.encrypt("P@ssw0rd");
        System.out.println(encrypt);
    }

    @Test
    public void testDecrypt() {
        String encrypt = jasyptStringEncryptor.decrypt("vc9prPjNGwRqIf5qFMpEKNh2YF44xr7b");
        System.out.println(encrypt);
    }

    @Test
    public void testEnvironment() {
        Environment bean = applicationContext.getBean(Environment.class);
        System.out.println(bean.getProperty("spring.datasource-emasdb.password"));
    }
}
