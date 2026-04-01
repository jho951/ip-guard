package com.ipguard.config;

import jakarta.servlet.http.HttpServletRequest;

public interface ClientIpResolver {
	String resolve(HttpServletRequest request);
}
