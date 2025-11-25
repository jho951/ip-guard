package com.ipguard.rules;

import com.ipguard.util.IpUtils;

/**
 * IP 범위 규칙
 */
public final class RangeIpRule implements IpRule {

	private final String raw;
	private final int startInt;
	private final int endInt;

	public RangeIpRule(String start, String end) {
		this.raw = start.trim() + "-" + end.trim();
		this.startInt = IpUtils.ipToInt(start.trim());
		this.endInt = IpUtils.ipToInt(end.trim());
	}

	@Override
	public IpRuleType type() {
		return IpRuleType.RANGE;
	}

	@Override
	public boolean matches(String clientIp) {
		int c = IpUtils.ipToInt(clientIp);
		return IpUtils.inRange(c, startInt, endInt);
	}

	@Override
	public String raw() {
		return raw;
	}
}
