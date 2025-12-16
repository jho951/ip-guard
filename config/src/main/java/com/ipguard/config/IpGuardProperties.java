package com.ipguard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ipguard")
public class IpGuardProperties {

	/**
	 * 룰이 비어있을 때 기본 허용 여부
	 */
	private boolean defaultAllow = false;

	/**
	 * ENV에서 룰을 읽을 때 사용할 키
	 */
	private String envKey = "IPGUARD_RULES";

	public boolean isDefaultAllow() {
		return defaultAllow;
	}

	public void setDefaultAllow(boolean defaultAllow) {
		this.defaultAllow = defaultAllow;
	}

	public String getEnvKey() {
		return envKey;
	}

	public void setEnvKey(String envKey) {
		this.envKey = envKey;
	}
}
