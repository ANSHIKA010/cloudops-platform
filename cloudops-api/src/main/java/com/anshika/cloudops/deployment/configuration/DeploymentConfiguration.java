package com.anshika.cloudops.deployment.configuration;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class DeploymentConfiguration {

	@Bean
	Clock deploymentClock() {
		return Clock.systemUTC();
	}
}
