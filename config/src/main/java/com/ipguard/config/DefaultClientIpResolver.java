package com.ipguard.config;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpParser;
import com.ipguard.core.rules.IpRule;
import com.ipguard.core.rules.RuleParser;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public final class DefaultClientIpResolver implements ClientIpResolver {

	private final IpGuardProperties.ClientIpStrategy strategy;
	private final List<IpRule> trustedProxyRules;

	public DefaultClientIpResolver(IpGuardProperties.ClientIpStrategy strategy, String trustedProxies) {
		this.strategy = strategy == null ? IpGuardProperties.ClientIpStrategy.REMOTE_ADDR_ONLY : strategy;
		this.trustedProxyRules = RuleParser.parse(normalizeTrustedProxies(trustedProxies));
	}

	@Override
	public String resolve(HttpServletRequest request) {
		if (request == null) {
			return null;
		}

		return switch (strategy) {
			case XFF_FIRST -> resolveXffFirst(request);
			case TRUSTED_PROXY_CHAIN -> resolveTrustedProxyChain(request);
			case REMOTE_ADDR_ONLY -> request.getRemoteAddr();
		};
	}

	private String resolveXffFirst(HttpServletRequest request) {
		String xff = request.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank()) {
			return xff.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	private String resolveTrustedProxyChain(HttpServletRequest request) {
		String xff = request.getHeader("X-Forwarded-For");
		String remoteAddr = request.getRemoteAddr();

		List<String> chain = new java.util.ArrayList<>();
		if (xff != null && !xff.isBlank()) {
			for (String part : xff.split(",")) {
				String trimmed = part.trim();
				if (!trimmed.isEmpty()) {
					chain.add(trimmed);
				}
			}
		}
		if (remoteAddr != null && !remoteAddr.isBlank()) {
			chain.add(remoteAddr.trim());
		}

		if (chain.isEmpty()) {
			return remoteAddr;
		}

		for (int i = chain.size() - 1; i >= 0; i--) {
			String candidate = chain.get(i);
			if (!isTrustedProxy(candidate)) {
				return candidate;
			}
		}

		return chain.get(0);
	}

	private boolean isTrustedProxy(String rawIp) {
		try {
			IpAddress ip = IpParser.parse(rawIp);
			for (IpRule rule : trustedProxyRules) {
				if (rule.matches(ip)) {
					return true;
				}
			}
		} catch (Exception ignored) {
			return false;
		}
		return false;
	}

	private static String normalizeTrustedProxies(String raw) {
		if (raw == null) {
			return "";
		}
		String normalized = raw.trim();
		if (normalized.isEmpty()) {
			return "";
		}
		return normalized.replace(',', '\n');
	}
}
