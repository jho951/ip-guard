package com.ipguard.core.engine;

import java.util.List;

import com.ipguard.core.rules.IpRuleParser;
import com.ipguard.core.util.IpUtils;

import com.ipguard.core.rules.IpRuleInterface;

/**
 * ip-guard 엔진.
 * - 생성 시 RuleSource에서 RuleSet을 한 번 로드하고 고정 사용 (v1: 리로드 없음)
 */
public final class IpGuardEngine {

	private final List<IpRuleInterface> rules;

	// 1) 가장 순수한 생성자: 이미 파싱된 룰 리스트
	public IpGuardEngine(List<IpRuleInterface> rules) {
		this.rules = List.copyOf(rules);
	}

	// 2) 편의 생성자: 룰 문자열 직접 넣기
	public static IpGuardEngine fromRawRules(String rawRules) {
		List<IpRuleInterface> rules = IpRuleParser.parse(rawRules);
		return new IpGuardEngine(rules);
	}

	// (선택) 3) RuleSource 기반 생성자 (파일/ENV/DB 어댑터용)
	public IpGuardEngine(RuleSource ruleSource) {
		this(ruleSource.loadRules());
	}

	public Decision decide(String rawIp) {
		String clientIp = IpUtils.normalizeClientIp(rawIp);
		if (clientIp == null || clientIp.isBlank()) {
			return Decision.DENY;
		}
		for (IpRuleInterface rule : rules) {
			if (rule.matches(clientIp)) {
				return Decision.ALLOW;
			}
		}
		return Decision.DENY;
	}

	// boolean 버전도 하나 있으면 사용성이 좋음
	public boolean isAllowed(String rawIp) {
		return decide(rawIp) == Decision.ALLOW;
	}
}
