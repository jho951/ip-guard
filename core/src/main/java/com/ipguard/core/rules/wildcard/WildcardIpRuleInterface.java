package com.ipguard.core.rules.wildcard;

import com.ipguard.core.util.IpUtils;
import com.ipguard.core.rules.IpRuleInterface;
import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;

/**
 * 와일드카드 IP 규칙 (10.* , 192.168.*.* 등).
 */
public final class WildcardIpRuleInterface implements IpRuleInterface {

	private final String raw;
	private final int baseInt;
	private final int maskInt;

	public WildcardIpRuleInterface(String wildcardRaw) {
		if (wildcardRaw == null || wildcardRaw.isBlank()) {
			throw new IpGuardException(ErrorCode.INVALID_RULE_SYNTAX, "Wildcard rule is blank");
		}

		this.raw = wildcardRaw.trim();
		String[] parts = this.raw.split("\\.");
		if (parts.length < 1 || parts.length > 4) {
			throw new IpGuardException(ErrorCode.INVALID_RULE_SYNTAX, "Invalid wildcard rule: " + raw);
		}

		String[] octets = new String[4];
		for (int i = 0; i < 4; i++) {
			octets[i] = (i < parts.length) ? parts[i].trim() : "*";
		}

		int base = 0;
		int mask = 0;

		for (int i = 0; i < 4; i++) {
			String o = octets[i];

			base <<= 8;
			mask <<= 8;

			if ("*".equals(o)) {
				base |= 0x00;
				mask |= 0x00;
			} else {
				int v;
				try {
					v = Integer.parseInt(o);
				} catch (NumberFormatException e) {
					throw new IpGuardException(ErrorCode.INVALID_RULE_SYNTAX, "IP를 찾을 수 없습니다. (wildcard 패턴): " + raw, e);
				}
				if (v < 0 || v > 255) {
					throw new IpGuardException(ErrorCode.INVALID_RULE_SYNTAX, "IP를 찾을 수 없습니다. (wildcard 패턴)" + raw);
				}
				base |= (v & 0xFF);
				mask |= 0xFF;
			}
		}

		this.baseInt = base;
		this.maskInt = mask;
	}

	@Override
	public boolean matches(String clientIp) {
		int c = IpUtils.ipToInt(clientIp);
		return (c & maskInt) == (baseInt & maskInt);
	}
}
