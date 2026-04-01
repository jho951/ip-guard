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
	private final ClientIpResolver clientIpResolver;
	private final BlockResponseWriter blockResponseWriter;

	public IpGuardFilter(IpGuardEngine engine, ClientIpResolver clientIpResolver, BlockResponseWriter blockResponseWriter) {
		this.engine = engine;
		this.clientIpResolver = clientIpResolver;
		this.blockResponseWriter = blockResponseWriter;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
		throws ServletException, IOException {

		String rawIp = clientIpResolver.resolve(req);
		var decision = engine.decide(rawIp);

		if (!decision.allowed()) {
			blockResponseWriter.write(res, decision);
			return;
		}

		chain.doFilter(req, res);
	}
}
