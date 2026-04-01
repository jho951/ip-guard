package com.ipguard.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultClientIpResolverTest {

	@Test
	void remote_addr_only_uses_remote_address() {
		DefaultClientIpResolver resolver = new DefaultClientIpResolver(
			IpGuardProperties.ClientIpStrategy.REMOTE_ADDR_ONLY,
			null
		);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRemoteAddr("203.0.113.10");
		request.addHeader("X-Forwarded-For", "198.51.100.20");

		assertEquals("203.0.113.10", resolver.resolve(request));
	}

	@Test
	void xff_first_uses_first_forwarded_value() {
		DefaultClientIpResolver resolver = new DefaultClientIpResolver(
			IpGuardProperties.ClientIpStrategy.XFF_FIRST,
			null
		);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRemoteAddr("203.0.113.10");
		request.addHeader("X-Forwarded-For", "198.51.100.20, 198.51.100.21");

		assertEquals("198.51.100.20", resolver.resolve(request));
	}

	@Test
	void trusted_proxy_chain_skips_trusted_proxies() {
		DefaultClientIpResolver resolver = new DefaultClientIpResolver(
			IpGuardProperties.ClientIpStrategy.TRUSTED_PROXY_CHAIN,
			"10.0.0.0/8\n192.168.0.0/16"
		);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRemoteAddr("10.0.0.5");
		request.addHeader("X-Forwarded-For", "198.51.100.20, 192.168.0.15");

		assertEquals("198.51.100.20", resolver.resolve(request));
	}
}
