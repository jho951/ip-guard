package com.ipguard.core.engine;

import java.util.List;

import com.ipguard.core.rules.IpRuleInterface;

/**
 * 규칙을 어디서 읽어오는지(파일, ENV, DB 등)를 추상화하는 SPI.
 * v1에서는 File/Env 기반 구현만 사용하고, 리로드는 없다.
 */
public interface RuleSource {
	List<IpRuleInterface> loadRules();
}
