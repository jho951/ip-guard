package com.ipguard.core.rules;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;
import com.ipguard.core.rules.cidr.CidrIpRuleInterface;
import com.ipguard.core.rules.range.RangeIpRuleInterface;
import com.ipguard.core.rules.single.SingleIpRuleInterface;
import com.ipguard.core.rules.wildcard.WildcardIpRuleInterface;

/**
 * 규칙 문자열을 IpRule 리스트로 파싱.
 * 지원 포맷:
 * - SINGLE: 1.2.3.4
 * - RANGE:  1.2.3.4-1.2.3.10
 * - CIDR:   192.168.0.0/24
 * - WILD:   10.* , 192.168.*.*
 * 구분자: | , \n , ,(comma)
 */
public final class IpRuleParser {

	private IpRuleParser() {}

	/**
	 * @param rawRules 규칙 문자열
	 */
	public static List<IpRuleInterface> parse(String rawRules) {
		if (rawRules == null || rawRules.isBlank()) return List.of();

		String normalized = rawRules
			.replace("\r\n", "\n")
			.replace(",", "|")
			.replace("\n", "|");

		String[] tokens = normalized.split("\\|");

		LinkedHashSet<String> uniq = new LinkedHashSet<>();
		for (String t : tokens) {
			if (t == null) continue;
			String x = t.trim();
			if (!x.isEmpty()) uniq.add(x);
		}

		List<IpRuleInterface> rules = new ArrayList<>();
		for (String token : uniq) {
			rules.add(parseOne(token));
		}
		return rules;
	}

	private static IpRuleInterface parseOne(String token) {
		if (token.contains("/")) {
			return new CidrIpRuleInterface(token);
		}
		if (token.contains("-")) {
			String[] se = token.split("-");
			if (se.length != 2) {
				throw new IpGuardException(ErrorCode.INVALID_RULE_SYNTAX, "잘못된 범위입니다: " + token);
			}
			return new RangeIpRuleInterface(se[0], se[1]);
		}
		if (token.contains("*")) {
			return new WildcardIpRuleInterface(token);
		}
		return new SingleIpRuleInterface(token);
	}
}
