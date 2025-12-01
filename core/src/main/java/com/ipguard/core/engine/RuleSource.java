package com.ipguard.core.engine;

import java.util.List;

import com.ipguard.core.rules.IpRuleInterface;

/**
 * 규칙을 어디서 읽어오는지(파일, ENV, DB 등)를 추상화하는 SPI.
 * v1에서는 파일/ENV 기반 구현만 사용.
 */
public interface RuleSource {

	/**
	 * 규칙 집합을 로드한다.
	 * 실패 시 IpGuardException을 던질 수 있다.
	 */
	List<IpRuleInterface> loadRules();
}
