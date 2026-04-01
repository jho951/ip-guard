package com.ipguard.config;

import com.ipguard.core.decision.Decision;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class JsonBlockResponseWriter implements BlockResponseWriter {

	@Override
	public void write(HttpServletResponse response, Decision decision) throws IOException {
		response.setStatus(403);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{\"message\":\"ACCESS_DENIED\",\"reason\":\"" + decision.reason() + "\"}");
	}
}
