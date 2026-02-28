package com.ipguard.core.rules;

import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;
import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;

import java.math.BigInteger;

public final class CidrIpRule implements IpRule {

	private final IpFamily family;
	private final int prefixLen;
	private final BigInteger mask;
	private final BigInteger network;

	public CidrIpRule(IpFamily family, int prefixLen, BigInteger ipValue128) {
		this.family = family;
		this.prefixLen = prefixLen;

		int max = (family == IpFamily.IPV4) ? 32 : 128;
		if (prefixLen < 0 || prefixLen > max) {
			throw new IpGuardException(ErrorCode.UNSUPPORTED_RULE_TYPE, "invalid prefixLen: " + prefixLen);
		}

		this.mask = prefixLen == 0
			? BigInteger.ZERO
			: BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE).xor(
			BigInteger.ONE.shiftLeft(128 - prefixLen).subtract(BigInteger.ONE)
		);

		this.network = ipValue128.and(mask);
	}

	@Override
	public IpFamily family() {
		return family;
	}

	@Override
	public boolean matches(IpAddress ip) {
		if (ip == null || ip.family() != family) return false;
		return ip.value128().and(mask).equals(network);
	}
}
