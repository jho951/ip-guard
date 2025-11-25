package com.ipguard.rules;

/**
 * 단일 IP 규칙 인터페이스
 */
public interface IpRule {

	/**
	 * @return 규칙 타입
	 * @see IpRuleType
	 */
	IpRuleType type();

	/**
	 * clientIp가 이 규칙에 매칭되는지 여부.
	 *
	 * @param clientIp normalized IPv4 string
	 * @return true if allowed by this rule
	 */
	boolean matches(String clientIp);

	/**
	 * @return 사람이 읽을 수 있게 표현
	 */
	String raw();
}
