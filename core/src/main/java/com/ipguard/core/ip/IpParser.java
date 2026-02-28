package com.ipguard.core.ip;

import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;

import java.math.BigInteger;
import java.net.InetAddress;

public final class IpParser {

	private IpParser() {}

	public static IpAddress parse(String raw) {
		if (raw == null) throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "ip is null");
		String s = normalize(raw);
		if (s.isEmpty()) throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "ip is blank");

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

	private static String normalize(String raw) {
		String s = raw.trim();
		if (s.isEmpty()) return s;

		if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
			s = s.substring(1, s.length() - 1).trim();
		}

		// [IPv6]:port 형태에서 IPv6만 추출
		if (s.startsWith("[")) {
			int end = s.indexOf(']');
			if (end > 1) {
				s = s.substring(1, end).trim();
			}
		}

		// IPv4:port 형태 지원
		if (isIpv4WithPort(s)) {
			int idx = s.lastIndexOf(':');
			s = s.substring(0, idx).trim();
		}

		// IPv6 zone 제거: fe80::1%en0
		int zone = s.indexOf('%');
		if (zone > 0) s = s.substring(0, zone);

		return s;
	}

	private static boolean isIpv4WithPort(String s) {
		if (s == null) return false;
		int idx = s.lastIndexOf(':');
		if (idx <= 0 || idx == s.length() - 1) return false;
		if (s.indexOf(':') != idx) return false;

		String host = s.substring(0, idx);
		String port = s.substring(idx + 1);
		if (!host.contains(".")) return false;

		for (int i = 0; i < port.length(); i++) {
			if (!Character.isDigit(port.charAt(i))) return false;
		}
		return true;
	}
}
