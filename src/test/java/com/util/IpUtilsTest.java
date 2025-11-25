package com.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ipguard.util.IpUtils;


class IpUtilsTest {

	// ---------------------------------------------------------------------
	// normalizeClientIp
	// ---------------------------------------------------------------------

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
	@DisplayName("normalizeClientIp: IPv6 loopback(::1)은 127.0.0.1로 변환")
	void normalizeClientIp_ipv6Loopback() {
		assertEquals("127.0.0.1", IpUtils.normalizeClientIp("::1"));
		assertEquals("127.0.0.1", IpUtils.normalizeClientIp("  ::1  "));
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


	// ---------------------------------------------------------------------
	// ipToInt
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("ipToInt: 정상 IPv4 변환")
	void ipToInt_validIpv4() {
		assertEquals((int)0x7F000001, IpUtils.ipToInt("127.0.0.1"));      // 2130706433
		assertEquals((int)0x00000000, IpUtils.ipToInt("0.0.0.0"));        // 0
		assertEquals((int)0xFFFFFFFF, IpUtils.ipToInt("255.255.255.255"));// -1
		assertEquals((int)0xC0A80001, IpUtils.ipToInt("192.168.0.1"));    // signed int로는 음수
	}

	@Test
	@DisplayName("ipToInt: 잘못된 IP면 IllegalArgumentException")
	void ipToInt_invalidIp() {
		assertThrows(IllegalArgumentException.class, () -> IpUtils.ipToInt("999.1.1.1"));
		assertThrows(IllegalArgumentException.class, () -> IpUtils.ipToInt("abc"));
		assertThrows(IllegalArgumentException.class, () -> IpUtils.ipToInt("1.2.3"));
	}

	@Test
	@DisplayName("ipToInt: IPv6 입력이면 IPv4 only 예외")
	void ipToInt_ipv6Throws() {
		assertThrows(IllegalArgumentException.class, () -> IpUtils.ipToInt("::1"));
		assertThrows(IllegalArgumentException.class, () -> IpUtils.ipToInt("2001:db8::1"));
	}


	// ---------------------------------------------------------------------
	// inRange
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("inRange: 범위 포함(경계 포함) true")
	void inRange_inclusive() {
		int start = IpUtils.ipToInt("10.0.0.1");
		int end   = IpUtils.ipToInt("10.0.0.10");

		assertTrue(IpUtils.inRange(IpUtils.ipToInt("10.0.0.1"), start, end));  // start 경계
		assertTrue(IpUtils.inRange(IpUtils.ipToInt("10.0.0.5"), start, end));  // 중간
		assertTrue(IpUtils.inRange(IpUtils.ipToInt("10.0.0.10"), start, end)); // end 경계
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
		int start = IpUtils.ipToInt("255.255.255.250"); // 매우 큰 값(음수 int)
		int end   = IpUtils.ipToInt("255.255.255.255"); // -1

		assertTrue(IpUtils.inRange(IpUtils.ipToInt("255.255.255.252"), start, end));
		assertTrue(IpUtils.inRange(IpUtils.ipToInt("255.255.255.255"), start, end));
		assertFalse(IpUtils.inRange(IpUtils.ipToInt("0.0.0.1"), start, end));
	}
}
