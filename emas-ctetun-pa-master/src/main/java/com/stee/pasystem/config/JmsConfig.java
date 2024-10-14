package com.stee.pasystem.config;

import com.stee.pasystem.utils.PaJmsMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.util.ErrorHandler;

import javax.jms.*;
import java.io.Serializable;


/**
 * @author Wang Yu
 * crated at 2021/2/16
 */
@Configuration
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JmsConfig {

    private final ContextConfig contextConfig;
    @Value("${stee.jms-qos-enable}")
    private boolean qosEnable;
    @Value("${stee.jms-message-time-to-live}")
    private Long jmsMessageTimeToLive;
    @Value("${stee.jndi-jms-factory}")
    private String jndiJmsFactory;

    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(
            @Qualifier("emasConnectionFactory") ConnectionFactory jmsConnectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);
        factory.setDestinationResolver(((session, destinationName, pubSubDomain) ->
                pubSubDomain ? lookupTopic(destinationName) : lookupQueue(destinationName)));
        factory.setErrorHandler(new DefaultErrorHandler());
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
        factory.setErrorHandler(new DefaultErrorHandler());
        configurer.configure(factory, jmsConnectionFactory);
        return factory;
    }

    @SneakyThrows
    @Bean
    public JmsTemplate queueTemplate(@Qualifier("emasConnectionFactory") ConnectionFactory jmsConnectionFactory, MessageConverter paMessageConverter) {
        return createJmsTemplate(jmsConnectionFactory, false, paMessageConverter);
    }

    @SneakyThrows
    @Bean
    public JmsTemplate topicTemplate(@Qualifier("emasConnectionFactory") ConnectionFactory jmsConnectionFactory, MessageConverter paMessageConverter) {
        return createJmsTemplate(jmsConnectionFactory, true, paMessageConverter);
    }

    @SneakyThrows
    @Bean
    public ConnectionFactory emasConnectionFactory() {
        return (TopicConnectionFactory) contextConfig
                .context()
                .lookup(jndiJmsFactory);
    }

    @SneakyThrows
    private Topic lookupTopic(String name) {
        return (Topic) contextConfig.context().lookup(name);
    }

    @SneakyThrows
    private Queue lookupQueue(String name) {
        return (Queue) contextConfig.context().lookup(name);
    }

    @Bean
    public MessageConverter paMessageConverter() {
        return new MessageConverter() {
            @NotNull
            @Override
            @SuppressWarnings("unchecked")
            public Message toMessage(@NotNull Object object, @NotNull Session session)
                    throws JMSException, MessageConversionException {
                PaJmsMessage<Serializable> transferMessage;
                if (object instanceof PaJmsMessage) {
                    transferMessage = (PaJmsMessage<Serializable>) object;
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

            @NotNull
            @Override
            public Object fromMessage(@NotNull Message message) throws JMSException, MessageConversionException {
                ObjectMessage objectMessage;
                if (message instanceof ObjectMessage) {
                    objectMessage = (ObjectMessage) message;
                    if (StringUtils.isEmpty(objectMessage.getJMSCorrelationID())) {
                        throw new MessageConversionException("Detected received message has " +
                                "empty correlationId");
                    }
                    return new PaJmsMessage<>(objectMessage.getObject(), objectMessage.getJMSCorrelationID());
                }
                throw new MessageConversionException("Detected received message is not a ObjectMessage");
            }
        };
    }

    private JmsTemplate createJmsTemplate(ConnectionFactory jmsConnectionFactory, boolean pubSub, MessageConverter paMessageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(jmsConnectionFactory);
        jmsTemplate.setPubSubDomain(pubSub);
        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.setExplicitQosEnabled(qosEnable);
        jmsTemplate.setTimeToLive(jmsMessageTimeToLive);
        jmsTemplate.setDestinationResolver(((session, destinationName, pubSubDomain) ->
                pubSubDomain ? lookupTopic(destinationName) : lookupQueue(destinationName)));
        jmsTemplate.setDestinationResolver(((session, destinationName, pubSubDomain) ->
                pubSubDomain ? lookupTopic(destinationName) : lookupQueue(destinationName)));
        jmsTemplate.setMessageConverter(paMessageConverter);
        return jmsTemplate;
    }

    private static class DefaultErrorHandler implements ErrorHandler {
        @Override
        public void handleError(@NotNull Throwable t) {
            log.error("Error while listening to the queue", t);
        }
    }

}
