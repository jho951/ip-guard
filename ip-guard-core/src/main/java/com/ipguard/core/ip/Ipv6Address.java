package com.ipguard.core.ip;

import java.math.BigInteger;

public final class Ipv6Address implements IpAddress {

	private final BigInteger v128;
	private final String normalized;

	Ipv6Address(BigInteger v128, String normalized) {
		this.v128 = v128;
		this.normalized = normalized;
	}

	@Override
	public IpFamily family() {
		return IpFamily.IPV6;
	}

	@Override
	public BigInteger value128() {
		return v128;
	}

	@Override
	public String normalized() {
		return normalized;
	}

	@Override
	public int compareTo(IpAddress other) {
		if (other == null) return 1;
		int f = this.family().compareTo(other.family());
		if (f != 0) return f;
		return this.v128.compareTo(other.value128());
	}
}
