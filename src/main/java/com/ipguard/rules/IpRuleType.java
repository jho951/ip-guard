package com.ipguard.rules;

/**
 * IP 규칙 타입.
 */
public enum IpRuleType {
	/** 단일 IP
	 * ex: 172.24.30.4
	 */
	SINGLE,
	/** CIDR 패턴
	 * ex: 192.168.0.0/24
	 */
	CIDR,
	/** 범위 패턴
	 * ex: 10.0.0.1-10.0.0.20
	 */
	RANGE,
	/** WILDCARD 패턴
	 * ex: 10.0.0.*
	 */
	WILDCARD
}
