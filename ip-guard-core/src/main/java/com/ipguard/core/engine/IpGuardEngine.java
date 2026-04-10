package com.ipguard.core.engine;

import com.ipguard.core.decision.Decision;
import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpParser;
import com.ipguard.core.rules.RuleParser;
import com.ipguard.core.rules.RuleSet;
import com.ipguard.spi.RuleSource;

/**
 * v2 IpGuardEngine
 * - IPv4/IPv6 통합
 * - defaultAllow 정책 명확화
 */
public final class IpGuardEngine {

	private final RuleSet rules;
	private final boolean defaultAllow;

	public IpGuardEngine(RuleSource ruleSource, boolean defaultAllow) {
		this.defaultAllow = defaultAllow;
		String raw = (ruleSource == null) ? "" : ruleSource.loadRaw();
		this.rules = new RuleSet(RuleParser.parse(raw));
	}

	public Decision decide(String rawIp) {

		final IpAddress ip;
		try {
			ip = IpParser.parse(rawIp);
		} catch (Exception e) {
			return Decision.deny("INVALID_IP");
		}

		if (rules.isEmpty()) {
			return defaultAllow
				? Decision.allow("DEFAULT_ALLOW")
				: Decision.deny("DEFAULT_DENY");
		}

		return rules.anyMatch(ip)
			? Decision.allow("MATCHED_RULE")
			: Decision.deny("NO_MATCH");
	}
}
