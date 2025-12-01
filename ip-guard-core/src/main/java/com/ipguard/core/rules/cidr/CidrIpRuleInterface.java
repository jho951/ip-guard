package com.ipguard.core.rules.cidr;

import com.ipguard.core.exception.IpGuardErrorCode;
import com.ipguard.core.exception.IpGuardException;

import com.ipguard.core.rules.IpRuleInterface;

import com.ipguard.core.util.IpUtils;

/**
 * CIDR 규칙
 * 예: 192.168.0.0/24
 */
public final class CidrIpRuleInterface implements IpRuleInterface {

	private final String raw;
	private final int maskInt;
	private final int networkInt;

	public CidrIpRuleInterface(String cidr) {
		if (cidr == null || cidr.isBlank()) {
			throw new IpGuardException(IpGuardErrorCode.NOT_FOUND_IP, "IP를 찾을 수 없습니다. (Cidr 패턴)");
		}

		this.raw = cidr.trim();

		String[] parts = this.raw.split("/");

		if (parts.length != 2) {
			throw new IpGuardException(IpGuardErrorCode.INVALID_RULE_SYNTAX, "CIDR 패턴 규칙에 맞지 않습니다: " + cidr);
		}

		int prefix;
		String baseIp = parts[0].trim();

		try {
			prefix = Integer.parseInt(parts[1].trim());
		} catch (NumberFormatException e) {
			throw new IpGuardException(IpGuardErrorCode.INVALID_RULE_SYNTAX, "Invalid CIDR prefix: " + cidr, e);
		}

		if (prefix < 0) {
			throw new IpGuardException(IpGuardErrorCode.UNSUPPORTED_IP_TYPE, "IP 범위는 0 이상이여야 합니다. (Cidr 패턴): " + cidr);
		}

		if (prefix > 32){
			throw new IpGuardException(IpGuardErrorCode.UNSUPPORTED_IP_TYPE, "IP 범위는 32 이하여야 합니다. (Cidr 패턴): " + cidr);
		}

		this.networkInt = IpUtils.ipToInt(baseIp);
		this.maskInt = (prefix == 0) ? 0 : (int) (0xFFFFFFFFL << (32 - prefix));
	}

	@Override
	public boolean matches(String clientIp) {
		int c = IpUtils.ipToInt(clientIp);
		return (c & maskInt) == (networkInt & maskInt);
	}
}
