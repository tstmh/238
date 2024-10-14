/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.integration;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;

import com.stee.emas.common.constants.Constants;

/**
 * Title: EMAS Enhancement Project
 * Description : CMH Message Sender
 * This class is used by CMH to put object in the queue
 * Copyright: Copyright (c) 2012
 * Company:STEE-InfoComm
 * @author Scindia
 * @since Sep 26, 2012
 * @version $LastChangedRevision: 920 $
 * 			$LastChangedDate: 2013-05-14 11:38:59 + 0800 (Tue, 14 May 2013) $
 * $LastChangedBy: scindia $
 * 
 */

public class CMHMessageSender {
	
	private static final Logger logger = LoggerFactory.getLogger(CMHMessageSender.class);
	
	private JmsTemplate awJmsTemplate;
	private JmsTemplate ticssJmsTemplate;
	private JmsTemplate emasccsJmsTemplate;	
	private JmsTemplate emasitptJmsTemplate;
	private JmsTemplate group1JmsTemplate;
	private JmsTemplate pmcsJmsTemplate;

	private JmsTemplate wmcsTemplate;
	private JmsTemplate lusTemplate;
	private int itptEquipStatus = Constants.EQUIP_STATUS_NG;
	
	
	public JmsTemplate getLusTemplate() {
		return lusTemplate;
	}

	public void setLusTemplate(JmsTemplate lusTemplate) {
		this.lusTemplate = lusTemplate;
	}

	public JmsTemplate getWmcsTemplate() {
		return wmcsTemplate;
	}

	public void setWmcsTemplate(JmsTemplate wmcsTemplate) {
		this.wmcsTemplate = wmcsTemplate;
	}

	public void setItptEquipStatus(int thisStatus) {
		itptEquipStatus	= thisStatus;
	}
	/**
	 * @param awJmsTemplate the awJmsTemplate to set
	 */
	public void setAwJmsTemplate(JmsTemplate awJmsTemplate) {
		this.awJmsTemplate = awJmsTemplate;
	}

	/**
	 * @param ticssJmsTemplate the ticssJmsTemplate to set
	 */
	public void setTicssJmsTemplate(JmsTemplate ticssJmsTemplate) {
		this.ticssJmsTemplate = ticssJmsTemplate;
	}

	/**
	 * @param emasccsJmsTemplate the emasccsJmsTemplate to set
	 */
	public void setEmasccsJmsTemplate(JmsTemplate emasccsJmsTemplate) {
		this.emasccsJmsTemplate = emasccsJmsTemplate;
	}	

	/**
	 * @param emasitptJmsTemplate the emasccsJmsTemplate to set
	 */
	public void setEmasitptJmsTemplate(JmsTemplate emasitptJmsTemplate) {
		this.emasitptJmsTemplate = emasitptJmsTemplate;
	}

	/**
	 * @param group1JmsTemplate the group1JmsTemplate to set
	 */
	public void setGroup1JmsTemplate(JmsTemplate group1JmsTemplate) {
		this.group1JmsTemplate = group1JmsTemplate;
	}
	
	/**
	 * @param pmcsJmsTemplate the pmcsJmsTemplate to set
	 */
	public void setPmcsJmsTemplate(JmsTemplate pmcsJmsTemplate) {
		this.pmcsJmsTemplate = pmcsJmsTemplate;
	}

	public void sendTicssJmsMessage(final Serializable obj, final String correlationId) {
		logger.info("Message Sent to MFELS Queue.....");
		logger.info("Message Sent to MFELS Queue, Message Id is::" + correlationId);
		ticssJmsTemplate.send(new MessageCreator() {
		    public Message createMessage(Session session) throws JMSException {
		        ObjectMessage message = session.createObjectMessage(obj);
		        message.setJMSCorrelationID(correlationId);
		        return message;
		    }
		});
		logger.debug("Exit sendTicssJmsMessage using send .....");
	}
	
	public void sendEmasCcsJmsMessage(final Serializable obj, final String correlationId) {
		logger.info("Message Sent to EMASCCS Queue.....");
		emasccsJmsTemplate.send(new MessageCreator() {
		    public Message createMessage(Session session) throws JMSException {
		        ObjectMessage message = session.createObjectMessage(obj);
		        message.setJMSCorrelationID(correlationId);
		        return message;
		    }
		});
		logger.debug("Exit sendEmasCcsJmsMessage using send .....");
	}
	
	// Added 04/03/22 GH - To implement new queues to ITPT-INTF
	public void sendEmasItptJmsMessage(final Serializable obj, final String correlationId) {
			logger.info("Message Sent to EMASITPT Queue.....");
			emasitptJmsTemplate.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage message = session.createObjectMessage(obj);
					message.setJMSCorrelationID(correlationId);
					return message;
				}
			});
			logger.debug("Exit sendEmasItptJmsMessage using send .....");
	}

	public void sendAWJmsMessage(Object pMessage, final String correlationId) {
		logger.info("Message Sent to AW Topic....." + correlationId);		
		awJmsTemplate.convertAndSend(pMessage, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				message.setJMSCorrelationID(correlationId);
				return message;
			}
		});
		logger.debug("Exit sendAWJmsMessage using convertAndSend ..... ");
	}
	
	public void sendGroup1JmsMessage(Object pMessage, final String correlationId) {
		logger.info("Message Sent to Group1 AW Topic.....");		
		group1JmsTemplate.convertAndSend(pMessage, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				
				message.setStringProperty("SENDER", "aw");
				message.setStringProperty("RECEIVER", "al");
				message.setStringProperty("TYPE", "12");
				message.setJMSCorrelationID(correlationId);
				return message;
			}
		});
		logger.debug("Exit sendGroup1JmsMessage using convertAndSend ..... ");
	}
	
	public void sendPmcsJmsMessage(Object pMessage, final String correlationId) {
		logger.info("Message Sent to Pmcs AW Topic.....");		
		pmcsJmsTemplate.convertAndSend(pMessage, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				
				message.setStringProperty("SENDER", "aw");
				message.setStringProperty("RECEIVER", "ax");
				message.setStringProperty("TYPE", "12");
				message.setJMSCorrelationID(correlationId);
				return message;
			}
		});
		logger.debug("Exit sendPmcsJmsMessage using convertAndSend ..... ");
	}

	public void sendWmcsJmsMessage(Object message, final String correlationId) {
		logger.info("Sending to WMCS:" + message);
		wmcsTemplate.convertAndSend(message, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				message.setJMSCorrelationID(correlationId);
				return message;
			}
		});
		logger.info("sent message " + message + " to wmcs queue ");
	}
	
	public void sendLusJmsMessage(Object message, final String correlationId) {
		logger.info("Sending to LUS:" + message);
		lusTemplate.convertAndSend(message, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				message.setJMSCorrelationID(correlationId);
				return message;
			}
		});
		logger.info("sent message " + message + " to lus queue ");
	}
}