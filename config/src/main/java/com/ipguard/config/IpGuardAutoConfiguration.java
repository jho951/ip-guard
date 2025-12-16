package com.ipguard.config;

import com.ipguard.core.engine.IpGuardEngine;
import com.ipguard.env.EnvRuleSource;
import com.ipguard.spi.RuleSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(IpGuardProperties.class)
public class IpGuardAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RuleSource ruleSource(IpGuardProperties props) {
		return new EnvRuleSource(props.getEnvKey());
	}

	@Bean
	@ConditionalOnMissingBean
	public IpGuardEngine ipGuardEngine(RuleSource ruleSource, IpGuardProperties props) {
		return new IpGuardEngine(ruleSource, props.isDefaultAllow());
	}

	@Bean
	@ConditionalOnMissingBean
	public IpGuardFilter ipGuardFilter(IpGuardEngine engine) {
		return new IpGuardFilter(engine);
	}
}
