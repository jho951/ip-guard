package com.ipguard.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ipguard.core.rules.IpRuleInterface;

public class IpGuardEngineTest {

	/**
	 * 간단한 테스트용 룰:
	 *  - exactMatchRule("127.0.0.1") => IP가 127.0.0.1이면 매칭
	 */
	private IpRuleInterface exactMatchRule(String expectedIp) {
		return clientIp -> expectedIp.equals(clientIp);
	}

	@Test
	void allow_whenAnyRuleMatches() {
		// given
		IpRuleInterface rule1 = exactMatchRule("127.0.0.1");
		IpRuleInterface rule2 = exactMatchRule("192.168.0.10");

		RuleSource ruleSource = () -> List.of(rule1, rule2);

		IpGuardEngine engine = new IpGuardEngine(ruleSource);

		Decision decision = engine.decide("127.0.0.1");

		assertEquals(Decision.ALLOW, decision);
	}

	@Test
	void deny_whenNoRuleMatches() {
		// given
		IpRuleInterface rule1 = exactMatchRule("10.0.0.1");
		IpRuleInterface rule2 = exactMatchRule("10.0.0.2");

		RuleSource ruleSource = () -> List.of(rule1, rule2);

		IpGuardEngine engine = new IpGuardEngine(ruleSource);

		// when
		Decision decision = engine.decide("127.0.0.1");

		// then
		assertEquals(Decision.DENY, decision);
	}

	@Test
	void deny_whenClientIpIsNullOrBlank_afterNormalize() {

		RuleSource emptyRuleSource = List::of;
		IpGuardEngine engine = new IpGuardEngine(emptyRuleSource);

		assertEquals(Decision.DENY, engine.decide(null));
		assertEquals(Decision.DENY, engine.decide(""));
		assertEquals(Decision.DENY, engine.decide("   "));
	}

	@Test
	void engineShouldUseDefensiveCopyOfRules() {
		// given
		List<IpRuleInterface> mutableRules = new ArrayList<>();
		mutableRules.add(exactMatchRule("127.0.0.1"));

		RuleSource ruleSource = () -> mutableRules;

		IpGuardEngine engine = new IpGuardEngine(ruleSource);

		// 엔진 생성 이후에 원본 리스트를 바꿔본다.
		mutableRules.clear();
		mutableRules.add(exactMatchRule("10.0.0.1"));

		// when: 127.0.0.1로 테스트
		Decision decision = engine.decide("127.0.0.1");

		// then: 생성 시점의 룰(127.0.0.1 허용)만 보고 있어야 하므로 ALLOW가 나와야 함
		assertEquals(Decision.ALLOW, decision);
	}

	@Test
	void allow_whenRuleMatchesNormalizedIp() {
		// 이 테스트는 IpUtils.normalizeClientIp의 동작을 어느 정도 믿고 가는 통합 성격.
		// 예: " 127.0.0.1 " -> "127.0.0.1" 같은 정규화를 한다고 가정.

		IpRuleInterface rule = exactMatchRule("127.0.0.1");
		RuleSource ruleSource = () -> List.of(rule);

		IpGuardEngine engine = new IpGuardEngine(ruleSource);

		Decision decision = engine.decide(" 127.0.0.1 "); // 양쪽 공백 포함

		assertEquals(Decision.ALLOW, decision);
	}
}
