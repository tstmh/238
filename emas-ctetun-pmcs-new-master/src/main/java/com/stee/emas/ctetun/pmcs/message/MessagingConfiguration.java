package com.stee.emas.ctetun.pmcs.message;

import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

@Configuration
@PropertySource("jms.properties")
//@EnableJms
public class MessagingConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(MessagingConfiguration.class);
	
	@Value("${jndiFactory}")
	private String jndiNamingFactoryInitial;

	@Value("${jndi.naming.factory.url.pkgs}")
	private String jndiNamingFactoryUrlPkgs;

	@Value("${jmsURL}")
	private String jndiNamingProviderUrl;

	@Value("${spring.jms.jndi-name}")
	private String jmsConnectionFactoryName;
	
	
	private Properties getJNDIProperties(){
		final Properties jndiProps = new Properties();
		jndiProps.setProperty(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		jndiProps.setProperty(Context.PROVIDER_URL, jndiNamingProviderUrl);
		
		return jndiProps;
	}
	
	@Bean(name="jndiTemplate")
	public JndiTemplate jndiTemplate() throws NamingException {

		logger.info("*** JNDI Template - {} = [{}] ***", Context.INITIAL_CONTEXT_FACTORY, jndiNamingFactoryInitial);
		logger.info("*** JNDI Template - {} = [{}] ***", Context.URL_PKG_PREFIXES, jndiNamingFactoryUrlPkgs);
		logger.info("*** JNDI Template - {} = [{}] ***", Context.PROVIDER_URL, jndiNamingProviderUrl);

		Properties environment = new Properties();
		if (   (jndiNamingFactoryInitial != null)
		    && (! jndiNamingFactoryInitial.trim().isEmpty()) ) {
			environment.put(Context.INITIAL_CONTEXT_FACTORY, jndiNamingFactoryInitial.trim());
		}
		/*if (   (jndiNamingFactoryUrlPkgs != null)
			    && (! jndiNamingFactoryUrlPkgs.trim().isEmpty()) ) {
			environment.put(Context.URL_PKG_PREFIXES, jndiNamingFactoryUrlPkgs.trim());
		}*/
		if (   (jndiNamingProviderUrl != null)
			    && (! jndiNamingProviderUrl.trim().isEmpty()) ) {
			environment.put(Context.PROVIDER_URL, jndiNamingProviderUrl.trim());
		}

		JndiTemplate jndiTemplate = new JndiTemplate();
		jndiTemplate.setEnvironment(environment);

		return jndiTemplate;
	}
	
	@Bean(name="jndiConnectionFactory")
	public JndiObjectFactoryBean jndiConnectionFactory(
			@Qualifier("jndiTemplate") JndiTemplate jndiTemplate) throws NamingException {

		JndiObjectFactoryBean connectionFactoryBean = new JndiObjectFactoryBean();
		connectionFactoryBean.setJndiTemplate(jndiTemplate);
		connectionFactoryBean.setJndiName(jmsConnectionFactoryName);
		connectionFactoryBean.afterPropertiesSet();
		return connectionFactoryBean;
	}

	/*@Bean(name="jmsConnectionFactory")
	@Primary
	public CachingConnectionFactory jmsConnectionFactory(
			@Qualifier("jndiConnectionFactory") JndiObjectFactoryBean jndiConnectionFactory) {

		CachingConnectionFactory cachingConnectionFactory =  new CachingConnectionFactory();
		ConnectionFactory connectionFactory = (ConnectionFactory) jndiConnectionFactory.getObject();
		cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
		cachingConnectionFactory.afterPropertiesSet();
		return cachingConnectionFactory;
	}*/
	
	@Bean(name="jmsConnectionFactory")
	@Primary
	public ConnectionFactory jmsConnectionFactory(@Qualifier("jndiConnectionFactory") JndiObjectFactoryBean jndiConnectionFactory) {
		ConnectionFactory connectionFactory = (ConnectionFactory) jndiConnectionFactory.getObject();
	    return (QueueConnectionFactory) connectionFactory;	    
	}
	
	/*@Bean
	  public ConnectionFactory connectionFactory() {
	    try {
	      System.out.println("Retrieving JMS queue with JNDI name: " + CONNECTION_FACTORY);
	      JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
	      jndiObjectFactoryBean.setJndiName(CONNECTION_FACTORY);

	      jndiObjectFactoryBean.setJndiEnvironment(getEnvProperties());
	      jndiObjectFactoryBean.afterPropertiesSet();

	      return (QueueConnectionFactory) jndiObjectFactoryBean.getObject();

	    } catch (NamingException e) {
	      System.out.println("Error while retrieving JMS queue with JNDI name: [" + CONNECTION_FACTORY + "]");
	    } catch (Exception ex) {
	      System.out.println("Error");
	    }
	    return null;
	  }*/
	
		
	@Bean(name="jmsListenerContainerFactory")
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(@Qualifier("jmsConnectionFactory") ConnectionFactory jmsConnectionFactory) {

	    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	    
	    factory.setConnectionFactory(jmsConnectionFactory);
	    JndiDestinationResolver jndiDestinationResolver = new JndiDestinationResolver();
	    try {
			jndiDestinationResolver.setJndiTemplate(jndiTemplate());
			jndiDestinationResolver.setJndiEnvironment(getJNDIProperties());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //jndiDestinationResolver.setJndiEnvironment(getEnvProperties());
	    factory.setDestinationResolver(jndiDestinationResolver);
	    return factory;
	  }
	
}