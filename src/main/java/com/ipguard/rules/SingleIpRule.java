package com.ipguard.rules;

import com.ipguard.util.IpUtils;

/**
 * 단일 IP 규칙.
 * raw : 규칙 원본 문자열
 * ipInt : IPv4 → int로 변환한 값
 */
public final class SingleIpRule implements IpRule {

	private final String raw;
	private final int ipInt;

	public SingleIpRule(String rawIp) {
		this.raw = rawIp.trim();
		this.ipInt = IpUtils.ipToInt(this.raw);
	}

	@Override
	public IpRuleType type() {
		return IpRuleType.SINGLE;
	}

	@Override
	public boolean matches(String clientIp) {
		int c = IpUtils.ipToInt(clientIp);
		return c == ipInt;
	}

	@Override
	public String raw() {
		return raw;
	}
}
