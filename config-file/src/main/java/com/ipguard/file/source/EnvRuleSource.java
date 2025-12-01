package com.ipguard.file.source;

import java.util.List;

import com.ipguard.core.engine.RuleSource;
import com.ipguard.core.exception.IpGuardErrorCode;
import com.ipguard.core.exception.IpGuardException;
import com.ipguard.core.rules.IpRuleInterface;
import com.ipguard.core.rules.IpRuleParser;
import com.ipguard.file.config.EnvConfig;

/**
 * ENV 기반 규칙 소스 (v1).
 *  - DEFAULT_IP 환경변수에서 규칙 문자열을 읽어 파싱.
 */
public final class EnvRuleSource implements RuleSource {

	@Override
	public List<IpRuleInterface> loadRules() {
		String normalized = EnvConfig.defaultIpRules();
		if (normalized == null || normalized.isBlank()) {
			throw new IpGuardException(
				IpGuardErrorCode.INVALID_ENV_VALUE,
				"DEFAULT_IP 환경변수가 비어 있거나 설정되지 않았습니다."
			);
		}
		List<IpRuleInterface> rules = IpRuleParser.parse(normalized);
		return List.copyOf(rules);
	}
}
