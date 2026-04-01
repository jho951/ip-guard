package com.ipguard.env;

import com.ipguard.spi.RuleSource;

public final class EnvRuleSource implements RuleSource {
	private final String envKey;

	/**
	 * 생성자
	 * @param envKey
	 */
	public EnvRuleSource(String envKey) {
		this.envKey = envKey;
	}

	@Override
	public String loadRaw() {
		if (envKey == null) return "";
		if (envKey.isBlank()) return "";
		String v = System.getenv(envKey);
		if(v == null) return "";
		return v;
	}
}
