package com.ipguard.core.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.ipguard.spi.RuleSource;

class IpGuardEngineTest {

	@Test
	void ipv4_single_match() {
		RuleSource src = () -> "127.0.0.1\n";
		IpGuardEngine engine = new IpGuardEngine(src, false);

		var d1 = engine.decide("127.0.0.1");
		var d2 = engine.decide("127.0.0.2");

		assertTrue(d1.allowed());
		assertFalse(d2.allowed());
	}

	@Test
	void empty_rules_default_allow() {
		RuleSource src = () -> "";
		IpGuardEngine engine = new IpGuardEngine(src, true);

		var d = engine.decide("1.2.3.4");
		assertTrue(d.allowed());
		assertEquals("DEFAULT_ALLOW", d.reason());
	}
}
