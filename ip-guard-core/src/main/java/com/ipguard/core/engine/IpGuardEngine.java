package com.ipguard.core.engine;

import com.ipguard.core.decision.Decision;
import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpParser;
import com.ipguard.core.rules.RuleParser;
import com.ipguard.core.rules.RuleSet;
import com.ipguard.spi.RuleSource;

/** 특정 IP 주소가 접속했을 때 통과시킬지 말지를 최종 판결하는 엔진 */
public final class IpGuardEngine {
	/** 허용하거나 차단할 IP들의 목록 세트 */
	private final RuleSet rules;
	/** 명단에 없는 IP가 왔을 때 어떻게 할 것인가?"에 대한 기본 설정값 */
	private final boolean defaultAllow;

	public IpGuardEngine(RuleSource ruleSource, boolean defaultAllow) {
		String raw = (ruleSource == null) ? "" : ruleSource.loadRaw();
		this.defaultAllow = defaultAllow;
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
