package com.ipguard.rules;

import com.ipguard.util.IpUtils;

/**
 * CIDR 규칙 (예: 192.168.0.0/24).
 */
public final class CidrIpRule implements IpRule {

	private final String raw;
	private final int networkInt;
	private final int maskInt;

	public CidrIpRule(String cidr) {
		this.raw = cidr.trim();

		String[] parts = this.raw.split("/");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid CIDR: " + cidr);
		}

		String baseIp = parts[0].trim();
		int prefix = Integer.parseInt(parts[1].trim());
		if (prefix < 0 || prefix > 32) {
			throw new IllegalArgumentException("Invalid CIDR prefix: " + cidr);
		}

		this.networkInt = IpUtils.ipToInt(baseIp);
		this.maskInt = prefix == 0 ? 0 : (int) (0xFFFFFFFFL << (32 - prefix));
	}

	@Override
	public IpRuleType type() {
		return IpRuleType.CIDR;
	}

	@Override
	public boolean matches(String clientIp) {
		int c = IpUtils.ipToInt(clientIp);
		return (c & maskInt) == (networkInt & maskInt);
	}

	@Override
	public String raw() {
		return raw;
	}
}
