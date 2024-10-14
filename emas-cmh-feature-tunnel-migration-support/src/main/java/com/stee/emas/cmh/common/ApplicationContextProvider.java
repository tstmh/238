package com.stee.emas.cmh.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Application Context Provider for CMH </p>
 * <p>This class is used to get Application Context 
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Dec 13, 2012
 * @version 1.0  
 *
 */

public class ApplicationContextProvider implements ApplicationContextAware {	

	private static ApplicationContext applicationContext;
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext pApplicationContext) throws BeansException {		
		applicationContext = pApplicationContext;
	}
}