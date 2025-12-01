package com.ipguard.core.rules.range;

import com.ipguard.core.exception.IpGuardErrorCode;
import com.ipguard.core.exception.IpGuardException;

import com.ipguard.core.rules.IpRuleInterface;
import com.ipguard.core.util.IpUtils;

/**
 * IP 범위 규칙 (start-end).
 */
public final class RangeIpRuleInterface implements IpRuleInterface {

	private final String raw;
	private final int startInt;
	private final int endInt;

	public RangeIpRuleInterface(String start, String end) {
		if (start == null) {
			throw new IpGuardException(IpGuardErrorCode.UNSUPPORTED_IP_TYPE,"시작 범위가 비어있습니다.");
		}
		if (end == null) {
			throw new IpGuardException(IpGuardErrorCode.UNSUPPORTED_IP_TYPE,"끝 범위가 비어있습니다.");
		}
		String s = start.trim();
		String e = end.trim();
		this.raw = s + "-" + e;
		this.startInt = IpUtils.ipToInt(s);
		this.endInt = IpUtils.ipToInt(e);
	}

	@Override
	public boolean matches(String clientIp) {
		int c = IpUtils.ipToInt(clientIp);
		return IpUtils.inRange(c, startInt, endInt);
	}

}
