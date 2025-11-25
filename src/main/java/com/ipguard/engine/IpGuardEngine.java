package com.ipguard.engine;

public final class IpGuardEngine {
	public boolean isAllowed(String clientIp) { }
	public IpGuardDecision decide(String clientIp) { } // 이유 포함
	public void refreshIfNeeded() { } // 리로드 전략에 따라
}