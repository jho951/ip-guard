package com.ipguard.env;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ipguard.env")
public class EnvRuleSourceProperties {

	private String envKey = "IPGUARD_RULES";

	public String getEnvKey() {
		return envKey;
	}

	public void setEnvKey(String envKey) {
		this.envKey = envKey;
	}
}
