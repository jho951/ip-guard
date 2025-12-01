package com.ipguard.core.engine;

import java.util.List;

import com.ipguard.core.util.IpUtils;

import com.ipguard.core.rules.IpRuleInterface;

/**
 * ip-guard 엔진.
 * - 생성 시 RuleSource에서 RuleSet을 한 번 로드하고 고정 사용 (v1: 리로드 없음)
 */
public final class IpGuardEngine {

	private final List<IpRuleInterface> rules;

	public IpGuardEngine(RuleSource ruleSource) {
		this.rules = List.copyOf(ruleSource.loadRules());
	}

	private boolean anyMatch(String clientIp) {
		for (IpRuleInterface rule : rules) {
			if (rule.matches(clientIp)) {
				return true;
			}
		}
		return false;
	}

	public Decision decide(String rawIp) {
		String clientIp = IpUtils.normalizeClientIp(rawIp);

		if (clientIp == null || clientIp.isBlank()) {
			return Decision.DENY;
		}

		return anyMatch(clientIp) ? Decision.ALLOW : Decision.DENY;
	}

}
