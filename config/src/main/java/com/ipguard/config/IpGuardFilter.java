package com.ipguard.config;

import com.ipguard.core.engine.IpGuardEngine;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class IpGuardFilter extends OncePerRequestFilter {

	private final IpGuardEngine engine;

	public IpGuardFilter(IpGuardEngine engine) {
		this.engine = engine;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
		throws ServletException, IOException {

		String rawIp = extractClientIp(req);
		var decision = engine.decide(rawIp);

		if (!decision.allowed()) {
			res.setStatus(403);
			res.setContentType("application/json;charset=UTF-8");
			res.getWriter().write("{\"message\":\"IP_BLOCKED\",\"reason\":\"" + decision.reason() + "\"}");
			return;
		}

		chain.doFilter(req, res);
	}

	private String extractClientIp(HttpServletRequest req) {
		String xff = req.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank()) {
			return xff.split(",")[0].trim();
		}
		return req.getRemoteAddr();
	}
}
