package com.stee.emas.ctetun.pmcs.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorServiceConfig {	
	
	@Bean("fixedThreadPool")
	public ScheduledExecutorService fixedThreadPool() {
		return Executors.newScheduledThreadPool(20);
		
	}
	
	@Bean("singleThreaded")
	public ScheduledExecutorService singleThreadExecutor() {
		return Executors.newSingleThreadScheduledExecutor();
	}
	
	@Bean("cachedThreadPool")
	public ExecutorService cachedThreadPool() {
		return Executors.newCachedThreadPool();
	}
}