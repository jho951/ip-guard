package com.ipguard.env;

import com.ipguard.spi.RuleSource;

public final class EnvRuleSource implements RuleSource {
	private final String envKey;

	public EnvRuleSource(String envKey) {
		this.envKey = envKey;
	}

	@Override
	public String loadRaw() {
		if (envKey == null || envKey.isBlank()) return "";
		String v = System.getenv(envKey);
		return v == null ? "" : v;
	}
}
