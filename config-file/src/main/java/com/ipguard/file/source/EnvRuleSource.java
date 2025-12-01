package com.ipguard.file.source;

import java.util.List;

import com.ipguard.core.engine.RuleSource;
import com.ipguard.core.rules.IpRuleParser;
import com.ipguard.core.rules.IpRuleInterface;

/**
 * ENV 기반 규칙 소스 (v1).
 *  - DEFAULT_IP 환경변수에서 규칙 문자열을 읽어 파싱.
 */
// ip-guard-source-env 모듈 안
public final class EnvRuleSource implements RuleSource {

	private final String envName; // 예: "DEFAULT_IP"

	public EnvRuleSource(String envName) {
		this.envName = envName;
	}

	@Override
	public List<IpRuleInterface> loadRules() {
		String raw = System.getenv(envName);
		return IpRuleParser.parse(raw);
	}
}

