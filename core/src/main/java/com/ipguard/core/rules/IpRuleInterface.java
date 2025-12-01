package com.ipguard.core.rules;

/**
 * 단일 IP 규칙 인터페이스
 */
public interface IpRuleInterface {
	/**
	 * clientIp가 매칭되는지 여부
	 * @param clientIp 정규화된 IPv4
	 * @return true if allowed by this rule
	 */
	boolean matches(String clientIp);
}
