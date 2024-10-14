package com.stee.emasext.emaslus.config;

import com.stee.emasext.emaslus.jms.LusJmsMessage;
import com.stee.emasext.emaslus.jms.handler.JmsErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.*;
import java.io.Serializable;

/**
 * @author Wang Yu
 * crated at 2021/2/16
 */
@Configuration
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ConditionalOnBean(WeblogicContextConfig.class)
public class JmsConfig {

    private final WeblogicContextConfig weblogicContextConfig;
    private final PropertiesConfig propertiesConfig;
    private final JmsErrorHandler jmsErrorHandler;

    // as researched: Spring can auto reconnect jms broker, but no log after reconnecting
    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(
            @Qualifier("emasConnectionFactory") ConnectionFactory jmsConnectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);
        factory.setDestinationResolver(((session, destinationName, pubSubDomain) ->
                pubSubDomain ? lookupTopic(destinationName) : lookupQueue(destinationName)));
        factory.setErrorHandler(jmsErrorHandler);
        configurer.configure(factory, jmsConnectionFactory);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> queueListenerFactory(
            @Qualifier("emasConnectionFactory") ConnectionFactory jmsConnectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(false);
        factory.setDestinationResolver(((session, destinationName, pubSubDomain) ->
                pubSubDomain ? lookupTopic(destinationName) : lookupQueue(destinationName)));
        factory.setErrorHandler(jmsErrorHandler);
        configurer.configure(factory, jmsConnectionFactory);
        return factory;
    }

    @SneakyThrows
    @Bean
    public JmsTemplate queueTemplate(@Qualifier("emasConnectionFactory") ConnectionFactory jmsConnectionFactory,
                                     MessageConverter lusMessageConverter) {
        return createJmsTemplate(jmsConnectionFactory, lusMessageConverter, false);
    }

    @SneakyThrows
    @Bean
    public JmsTemplate topicTemplate(@Qualifier("emasConnectionFactory") ConnectionFactory jmsConnectionFactory,
                                     MessageConverter lusMessageConverter) {
        return createJmsTemplate(jmsConnectionFactory, lusMessageConverter, true);
    }

    @SneakyThrows
    @Bean
    public ConnectionFactory emasConnectionFactory() {
        return (QueueConnectionFactory) weblogicContextConfig
                .context()
                .lookup(propertiesConfig.getJndiJmsFactory());
    }

    @SneakyThrows
    private Topic lookupTopic(String name) {
        return (Topic) weblogicContextConfig.context().lookup(name);
    }

    @SneakyThrows
    private Queue lookupQueue(String name) {
        return (Queue) weblogicContextConfig.context().lookup(name);
    }

    @Bean
    public MessageConverter lusMessageConverter() {
        return new MessageConverter() {
            @Override
            @SuppressWarnings("unchecked")
            public Message toMessage(Object object, Session session)
                    throws JMSException, MessageConversionException {
                LusJmsMessage<Serializable> transferMessage;
                if (object instanceof LusJmsMessage) {
                    transferMessage = (LusJmsMessage<Serializable>) object;
                    if (StringUtils.isEmpty(transferMessage.getCorrelationId())) {
                        throw new MessageConversionException("Detected the object has empty correlationId");
                    }
                    ObjectMessage message = session.createObjectMessage(transferMessage.getData());
                    message.setJMSCorrelationID(transferMessage.getCorrelationId());
                    return message;
                }
                throw new MessageConversionException("Detected the object is not " +
                        "assigned from LusJmsMessage");
            }

            @Override
            public Object fromMessage(Message message) throws JMSException, MessageConversionException {
                ObjectMessage objectMessage;
                if (message instanceof ObjectMessage) {
                    objectMessage = (ObjectMessage) message;
                    if (StringUtils.isEmpty(objectMessage.getJMSCorrelationID())) {
                        throw new MessageConversionException("Detected received message has " +
                                "empty correlationId");
                    }
                    return new LusJmsMessage<>(objectMessage.getObject(), objectMessage.getJMSCorrelationID());
                }
                throw new MessageConversionException("Detected received message is not a ObjectMessage");
            }
        };
    }

    private JmsTemplate createJmsTemplate(ConnectionFactory jmsConnectionFactory, MessageConverter lusMessageConverter,
                                          boolean pubSub) {
        JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory);
        jmsTemplate.setPubSubDomain(pubSub);
        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.setExplicitQosEnabled(propertiesConfig.isJmsQosEnable());
        jmsTemplate.setTimeToLive(propertiesConfig.getJmsMessageTimeToLive());
        jmsTemplate.setDestinationResolver(((session, destinationName, pubSubDomain) ->
                pubSubDomain ? lookupTopic(destinationName) : lookupQueue(destinationName)));
        jmsTemplate.setDestinationResolver(((session, destinationName, pubSubDomain) ->
                pubSubDomain ? lookupTopic(destinationName) : lookupQueue(destinationName)));
        jmsTemplate.setMessageConverter(lusMessageConverter);
        return jmsTemplate;
    }

}
