package com.ipguard.core.rules;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;
import com.ipguard.core.ip.IpRange;

public final class RangeIpRule implements IpRule {

	private final IpRange range;

	public RangeIpRule(IpRange range) {
		this.range = range;
	}

	@Override
	public IpFamily family() {
		return range.family();
	}

	@Override
	public boolean matches(IpAddress ip) {
		return range.contains(ip);
	}
}
