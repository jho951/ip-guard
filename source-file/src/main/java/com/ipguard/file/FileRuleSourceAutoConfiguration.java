package com.ipguard.file;

import com.ipguard.spi.RuleSource;

import java.nio.file.Path;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(FileRuleSourceProperties.class)
@ConditionalOnProperty(prefix = "ipguard.file", name = "path")
@AutoConfigureAfter(name = "com.ipguard.env.EnvRuleSourceAutoConfiguration")
public class FileRuleSourceAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RuleSource ruleSource(FileRuleSourceProperties props) {
		return new FileRuleSource(Path.of(props.getPath()));
	}
}
