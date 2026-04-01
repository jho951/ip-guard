package com.ipguard.env;

import com.ipguard.spi.RuleSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(EnvRuleSourceProperties.class)
public class EnvRuleSourceAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RuleSource ruleSource(EnvRuleSourceProperties props) {
		return new EnvRuleSource(props.getEnvKey());
	}
}
