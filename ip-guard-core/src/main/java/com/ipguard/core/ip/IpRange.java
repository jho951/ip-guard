package com.ipguard.core.ip;

import java.math.BigInteger;

public final class IpRange {
	private final IpFamily family;
	private final BigInteger start;
	private final BigInteger end;

	public IpRange(IpFamily family, BigInteger start, BigInteger end) {
		if (family == null) throw new IllegalArgumentException("family required");
		if (start == null || end == null) throw new IllegalArgumentException("start/end required");
		if (start.compareTo(end) > 0) throw new IllegalArgumentException("start must be <= end");
		this.family = family;
		this.start = start;
		this.end = end;
	}

	public IpFamily family() {
		return family;
	}

	public boolean contains(IpAddress ip) {
		if (ip == null || ip.family() != family) return false;
		var v = ip.value128();
		return v.compareTo(start) >= 0 && v.compareTo(end) <= 0;
	}
}
