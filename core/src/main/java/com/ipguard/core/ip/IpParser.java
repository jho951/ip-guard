package com.ipguard.core.ip;

import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;

import java.math.BigInteger;
import java.net.InetAddress;

public final class IpParser {

	private IpParser() {}

	public static IpAddress parse(String raw) {
		if (raw == null) throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "ip is null");
		String s = raw.trim();
		if (s.isEmpty()) throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "ip is blank");

		// IPv6 zone 제거: fe80::1%en0
		int zone = s.indexOf('%');
		if (zone > 0) s = s.substring(0, zone);

		try {
			InetAddress addr = InetAddress.getByName(s);
			byte[] bytes = addr.getAddress(); // 4 or 16

			if (bytes.length == 4) {
				BigInteger v = new BigInteger(1, bytes);
				return new Ipv4Address(v);
			}
			if (bytes.length == 16) {
				BigInteger v = new BigInteger(1, bytes);
				return new Ipv6Address(v, addr.getHostAddress());
			}
			throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "unsupported ip length: " + bytes.length);
		} catch (Exception e) {
			throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "invalid ip: " + raw);
		}
	}
}
