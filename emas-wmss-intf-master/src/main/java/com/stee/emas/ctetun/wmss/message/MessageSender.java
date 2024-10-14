package com.stee.emas.ctetun.wmss.message;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);	
	
	@Autowired
	JmsTemplate jmsTemplate;	
	
	public void sendCmhJmsMessage (final Serializable obj) {
		logger.info("Calling sendCmhJmsMessage .....");
		
		//jmsTemplate.setDefaultDestinationName("jms/wmcs_cmh_queue");		
		jmsTemplate.convertAndSend(obj);
	}
	
	
	public void sendCmhJmsMessage(final Serializable obj, final String correlationId) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside sendCmhJmsMessage using send -> Start");
		}
		/*jmsTemplate.convertAndSend(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage(obj);
				message.setJMSCorrelationID(correlationId);
				return message;
			}
		});*/
		
		jmsTemplate.convertAndSend(obj, new MessagePostProcessor() {
			  public Message postProcessMessage(Message message) throws JMSException {
			    //message.setIntProperty("ID", 9999);
			    message.setJMSCorrelationID(correlationId);
			    return message;
			    }
			  });
			
		
		if (logger.isInfoEnabled()) {
			logger.info("Exit sendCmhJmsMessage using send ");
		}		
	}
}
