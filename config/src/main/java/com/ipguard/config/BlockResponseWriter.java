package com.ipguard.config;

import com.ipguard.core.decision.Decision;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface BlockResponseWriter {
	void write(HttpServletResponse response, Decision decision) throws IOException;
}
