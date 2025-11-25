package com.ipguard.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ipguard.exception.IpGuardErrorCode;
import com.ipguard.exception.IpGuardException;

/**
 * IP 관련 유틸.
 * - IPv4 문자열 → int 변환
 * - IPv6 루프백(::1) 등 최소 정규화
 */
public final class IpUtils {

	private IpUtils() {}

	/**
	 * 클라이언트 IP를 최소 정규화한다.
	 * <ul>
	 *   <li>::1 → 127.0.0.1</li>
	 *   <li>IPv6 mapped IPv4(예: ::ffff:192.168.0.1) → 192.168.0.1</li>
	 * </ul>
	 *
	 * @param ip raw client ip
	 * @return normalized ip (null if input null/blank)
	 */
	public static String normalizeClientIp(String ip) {
		if (ip == null || ip.isBlank()) return null;
		String x = ip.trim();

		if ("::1".equals(x)) return "127.0.0.1";

		int lastColon = x.lastIndexOf(':');
		if (lastColon >= 0 && x.contains(".")) {
			String tail = x.substring(lastColon + 1);
			if (tail.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
				return tail;
			}
		}
		return x;
	}

	/**
	 * IPv4 문자열을 int로 변환한다. (부호 없는 32bit)
	 *
	 * @param ip IPv4 string
	 * @return unsigned int in signed int container
	 * @throws IllegalArgumentException invalid ip
	 */
	public static int ipToInt(String ip) {
		try {
			byte[] b = InetAddress.getByName(ip).getAddress();
			if (b.length != 4) {
				throw new IllegalArgumentException("IPv4 only: " + ip);
			}
			return ((b[0] & 0xFF) << 24)
				| ((b[1] & 0xFF) << 16)
				| ((b[2] & 0xFF) <<  8)
				|  (b[3] & 0xFF);
		} catch (UnknownHostException e) {
			throw new IpGuardException(IpGuardErrorCode.INVALID_IP_ADDRESS, ip, e);
		}
	}

	/**
	 * [start, end] 범위에 target이 포함되는지 검사
	 */
	public static boolean inRange(int target, int start, int end) {
		long t = target & 0xFFFFFFFFL;
		long s = start & 0xFFFFFFFFL;
		long e = end & 0xFFFFFFFFL;
		return s <= t && t <= e;
	}
}
