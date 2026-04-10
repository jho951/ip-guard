package com.ipguard.core.ip;

import java.math.BigInteger;

public final class Ipv4Address implements IpAddress {

	private final BigInteger v128;

	Ipv4Address(BigInteger v128) {
		this.v128 = v128;
	}

	@Override
	public IpFamily family() {
		return IpFamily.IPV4;
	}

	@Override
	public BigInteger value128() {
		return v128;
	}

	@Override
	public String normalized() {
		long v = v128.longValue(); // IPv4는 32비트 범위라 안전
		return ((v >> 24) & 0xff) + "." + ((v >> 16) & 0xff) + "." + ((v >> 8) & 0xff) + "." + (v & 0xff);
	}

	@Override
	public int compareTo(IpAddress other) {
		if (other == null) return 1;
		int f = this.family().compareTo(other.family());
		if (f != 0) return f;
		return this.v128.compareTo(other.value128());
	}
}
