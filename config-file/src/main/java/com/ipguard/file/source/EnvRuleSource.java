package com.ipguard.file.source;

import java.util.List;

import com.ipguard.core.engine.RuleSource;
import com.ipguard.core.exception.IpGuardErrorCode;
import com.ipguard.core.exception.IpGuardException;
import com.ipguard.core.rules.IpRuleInterface;
import com.ipguard.core.rules.IpRuleParser;
import com.ipguard.file.config.EnvConfig;

/**
 * ENV 기반 RuleSource 구현체.
 * - 생성 시 어떤 환경변수에서 규칙 문자열을 읽을지(envName)를 넘겨받는다.
 * - loadRules() 호출 시:
 *    1) 환경변수 값을 읽고
 *    2) 정규화한 뒤
 *    3) IpRuleParser.parse(...)로 파싱해 List<IpRuleInterface>로 반환한다.
 */
public final class EnvRuleSource implements RuleSource {

	private final String envName;

	public String getEnvName() {return envName;}

	/**
	 * @param envName 규칙 문자열을 담고 있는 환경변수 이름 (예: "DEFAULT_IP")
	 */
	public EnvRuleSource(String envName) {
		if (envName == null || envName.isBlank()) {
			throw new IllegalArgumentException("envName must not be null or blank");
		}
		this.envName = envName;
	}

	@Override
	public List<IpRuleInterface> loadRules() {
		String normalized = EnvConfig.normalizedEnvRules(envName);
		if (normalized == null || normalized.isBlank()) {
			throw new IpGuardException(
				IpGuardErrorCode.INVALID_ENV_VALUE,
				"환경변수 [" + envName + "]가 비어 있거나 설정되지 않았습니다."
			);
		}
		List<IpRuleInterface> rules = IpRuleParser.parse(normalized);
		// 방어적 복사: 외부에서 리스트 수정 못 하게
		return List.copyOf(rules);
	}
}
