package com.ipguard.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;

class IpUtilsTest {

	/**
	 * Ip 정규화 테스트
	 * @see IpUtils#normalizeClientIp(String)
	 */
	@Test
	@DisplayName("normalizeClientIp: null/blank이면 null 반환")
	void normalizeClientIp_nullOrBlank() {
		assertNull(IpUtils.normalizeClientIp(null));
		assertNull(IpUtils.normalizeClientIp(""));
		assertNull(IpUtils.normalizeClientIp("   "));
	}

	@Test
	@DisplayName("normalizeClientIp: IPv6 제공 시 에러 발생")
	void normalizeClientIp_ipv6Loopback() {
		assertThrows(IpGuardException.class, () -> IpUtils.normalizeClientIp("::1"));
	}

	@Test
	@DisplayName("normalizeClientIp: IPv6 mapped IPv4는 IPv4로 추출")
	void normalizeClientIp_ipv6MappedIpv4() {
		assertEquals("192.168.0.1", IpUtils.normalizeClientIp("::ffff:192.168.0.1"));
		assertEquals("10.0.0.7", IpUtils.normalizeClientIp("0:0:0:0:0:ffff:10.0.0.7"));
	}

	@Test
	@DisplayName("normalizeClientIp: 일반 IPv4/IPv6는 trim 후 그대로 반환")
	void normalizeClientIp_normal() {
		assertEquals("1.2.3.4", IpUtils.normalizeClientIp("1.2.3.4"));
		assertEquals("1.2.3.4", IpUtils.normalizeClientIp(" 1.2.3.4 "));
		assertEquals("2001:db8::1", IpUtils.normalizeClientIp("2001:db8::1"));
	}

	/**
	 * IPv4 문자열을 int로 변환한다. (부호 없는 32bit)
	 * @see IpUtils#ipToInt(String)
	 */
	@Test
	@DisplayName("ipToInt: 정상 IPv4 변환")
	void ipToInt_validIpv4() {
		assertEquals((int)0x7F000001, IpUtils.ipToInt("127.0.0.1"));
		assertEquals((int)0x00000000, IpUtils.ipToInt("0.0.0.0"));
		assertEquals((int)0xFFFFFFFF, IpUtils.ipToInt("255.255.255.255"));;
	}

	@Test
	@DisplayName("ipToInt: (Case A) 완전히 잘못된 입력 -> INVALID_IP_ADDRESS 예외 발생")
	void ipToInt_invalidIpAddress() {
		IpGuardException exception = assertThrows(IpGuardException.class, () -> IpUtils.ipToInt("abs"));
		assertEquals(ErrorCode.INVALID_IP_ADDRESS, exception.getErrorCode());
	}


	@Test
	@DisplayName("ipToInt: IPv6 입력이면 IPv4 only 예외")
	void ipToInt_ipv6Throws() {
		IpGuardException exception = assertThrows(IpGuardException.class,() -> IpUtils.ipToInt("::1"));
		assertEquals(ErrorCode.UNSUPPORTED_IP_TYPE, exception.getErrorCode());
	}


	// ---------------------------------------------------------------------
	// inRange
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("inRange: 범위 포함(경계 포함) true")
	void inRange_inclusive() {
		int start = IpUtils.ipToInt("10.0.0.1");
		int end   = IpUtils.ipToInt("10.0.0.10");

		assertTrue(IpUtils.inRange(IpUtils.ipToInt("10.0.0.1"), start, end));
		assertTrue(IpUtils.inRange(IpUtils.ipToInt("10.0.0.5"), start, end));
		assertTrue(IpUtils.inRange(IpUtils.ipToInt("10.0.0.10"), start, end));
	}

	@Test
	@DisplayName("inRange: 범위 밖 false")
	void inRange_outside() {
		int start = IpUtils.ipToInt("10.0.0.1");
		int end   = IpUtils.ipToInt("10.0.0.10");

		assertFalse(IpUtils.inRange(IpUtils.ipToInt("10.0.0.0"), start, end));
		assertFalse(IpUtils.inRange(IpUtils.ipToInt("10.0.0.11"), start, end));
	}

	@Test
	@DisplayName("inRange: unsigned 비교가 필요한 값도 정상 동작")
	void inRange_unsignedCheck() {
		int start = IpUtils.ipToInt("255.255.255.250");
		int end   = IpUtils.ipToInt("255.255.255.255");

		assertTrue(IpUtils.inRange(IpUtils.ipToInt("255.255.255.252"), start, end));
		assertTrue(IpUtils.inRange(IpUtils.ipToInt("255.255.255.255"), start, end));
		assertFalse(IpUtils.inRange(IpUtils.ipToInt("0.0.0.1"), start, end));
	}
}
