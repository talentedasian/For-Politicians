package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.example.demo.logger.PoliticiansLogger;

@Configuration
@EnableAspectJAutoProxy
public class AopConfiguration {

	@Bean
	public PoliticiansLogger polLogger() {
		return new PoliticiansLogger();
	}
}
