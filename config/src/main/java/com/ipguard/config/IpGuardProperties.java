package com.ipguard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ipguard")
public class IpGuardProperties {

	/**
	 * 룰이 비어있을 때 기본 허용 여부
	 */
	private boolean defaultAllow = false;

	/**
	 * 클라이언트 IP 추출 전략
	 */
	private ClientIpStrategy clientIpStrategy = ClientIpStrategy.REMOTE_ADDR_ONLY;

	/**
	 * trusted-proxy-chain 전략에서 신뢰할 프록시 목록
	 * 한 줄/쉼표 구분으로 입력할 수 있다.
	 */
	private String trustedProxies = "";

	public boolean isDefaultAllow() {
		return defaultAllow;
	}

	public void setDefaultAllow(boolean defaultAllow) {
		this.defaultAllow = defaultAllow;
	}

	public ClientIpStrategy getClientIpStrategy() {
		return clientIpStrategy;
	}

	public void setClientIpStrategy(ClientIpStrategy clientIpStrategy) {
		this.clientIpStrategy = clientIpStrategy;
	}

	public String getTrustedProxies() {
		return trustedProxies;
	}

	public void setTrustedProxies(String trustedProxies) {
		this.trustedProxies = trustedProxies;
	}

	public enum ClientIpStrategy {
		REMOTE_ADDR_ONLY,
		XFF_FIRST,
		TRUSTED_PROXY_CHAIN
	}
}
