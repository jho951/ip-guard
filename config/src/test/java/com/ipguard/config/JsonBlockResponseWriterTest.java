package com.ipguard.config;

import com.ipguard.core.decision.Decision;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonBlockResponseWriterTest {

	@Test
	void writes_default_block_response_json() throws Exception {
		JsonBlockResponseWriter writer = new JsonBlockResponseWriter();
		MockHttpServletResponse response = new MockHttpServletResponse();

		writer.write(response, Decision.deny("NO_MATCH"));

		assertEquals(403, response.getStatus());
		assertEquals("application/json;charset=UTF-8", response.getContentType());
		assertEquals("{\"message\":\"ACCESS_DENIED\",\"reason\":\"NO_MATCH\"}", response.getContentAsString());
	}
}
