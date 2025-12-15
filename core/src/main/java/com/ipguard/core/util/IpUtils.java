package com.ipguard.core.util;

import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP 관련 유틸.
 */
public final class IpUtils {

	private IpUtils() {}

	/**
	 * 클라이언트 IP를 최소 정규화.
	 * - ::1 → 127.0.0.1
	 * - ::ffff:192.168.0.1 → 192.168.0.1
	 */
	public static String normalizeClientIp(String ip) {
		if (ip == null || ip.isBlank()) return null;
		String x = ip.trim();

		if ("::1".equals(x)) throw new IpGuardException(ErrorCode.UNSUPPORTED_IP_TYPE, "IPv4만 지원합니다.: " + ip);

		int lastColon = x.lastIndexOf(':');
		if (lastColon >= 0 && x.contains(".")) {
			String tail = x.substring(lastColon + 1);
			if (tail.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) return tail;
		}
		return x;
	}

	/**
	 * IPv4 문자열을 int로 변환한다. (부호 없는 32bit)
	 */
	public static int ipToInt(String ip) {
		try {
			byte[] b = InetAddress.getByName(ip).getAddress();
			if (b.length != 4) {
				throw new IpGuardException(ErrorCode.UNSUPPORTED_IP_TYPE, "IPv4 형식이 아닙니다.: " + ip);
			}
			return ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) <<  8) |  (b[3] & 0xFF);
		} catch (UnknownHostException e) {
			throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "잘못된 IP 주소입니다: " + ip, e);
		}
	}

	/**
	 * [start, end] 범위에 target이 포함되는지 검사 (unsigned 비교).
	 * 부호 없는 0~4,294,967,295 범위로 비교하기 위해 비트를 long으로 확장
	 * @param target 범위 검사를 할 IP
	 * @param start 시작 범위
	 * @param end 끝 범위
	 */
	public static boolean inRange(int target, int start, int end) {
		long t = target & 0xFFFFFFFFL;
		long s = start & 0xFFFFFFFFL;
		long e = end & 0xFFFFFFFFL;
		return s <= t && t <= e;
	}
}
