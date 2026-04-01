package com.ipguard.config;

import com.ipguard.core.engine.IpGuardEngine;
import com.ipguard.spi.RuleSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(name = {
	"com.ipguard.env.EnvRuleSourceAutoConfiguration",
	"com.ipguard.file.FileRuleSourceAutoConfiguration"
})
@EnableConfigurationProperties(IpGuardProperties.class)
@ConditionalOnBean(RuleSource.class)
public class IpGuardAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ClientIpResolver clientIpResolver(IpGuardProperties props) {
		return new DefaultClientIpResolver(props.getClientIpStrategy(), props.getTrustedProxies());
	}

	@Bean
	@ConditionalOnMissingBean
	public BlockResponseWriter blockResponseWriter() {
		return new JsonBlockResponseWriter();
	}

	@Bean
	@ConditionalOnMissingBean
	public IpGuardEngine ipGuardEngine(RuleSource ruleSource, IpGuardProperties props) {
		return new IpGuardEngine(ruleSource, props.isDefaultAllow());
	}

	@Bean
	@ConditionalOnMissingBean
	public IpGuardFilter ipGuardFilter(IpGuardEngine engine, ClientIpResolver clientIpResolver, BlockResponseWriter blockResponseWriter) {
		return new IpGuardFilter(engine, clientIpResolver, blockResponseWriter);
	}
}
