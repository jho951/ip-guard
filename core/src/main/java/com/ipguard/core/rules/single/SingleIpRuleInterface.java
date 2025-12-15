package com.ipguard.core.rules.single;

import com.ipguard.core.rules.IpRuleInterface;

import com.ipguard.core.util.IpUtils;

import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;

/**
 * 단일 IP 규칙.
 */
public final class SingleIpRuleInterface implements IpRuleInterface {

	private final int ipInt;
	private final String raw;

	public SingleIpRuleInterface(String rawIp) {
		if (rawIp == null || rawIp.isBlank()) {
			throw new IpGuardException(ErrorCode.UNSUPPORTED_RULE_TYPE, "단일 IP가 빈 값입니다.");
		}

		this.raw = rawIp.trim();
		this.ipInt = IpUtils.ipToInt(this.raw);
	}

	@Override
	public boolean matches(String clientIp) {
		int c = IpUtils.ipToInt(clientIp);
		return c == ipInt;
	}

}
