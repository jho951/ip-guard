package com.ipguard.core.engine;

/**
 * IP 판정 결과.
 * ALLOW, DENY, UNKNOWN(규칙 매칭 없음) 세 가지 상태.
 */
public enum Decision {
	ALLOW,
	DENY;

	/**
	 * UNKNOWN일 때 defaultAllow 설정에 따라 최종 허용 여부를 계산할 때 사용할 수 있는 헬퍼.
	 */
	public boolean isAllowedWithDefault(boolean defaultAllow) {
		if (this == ALLOW) return true;
		if (this == DENY) return false;
		return defaultAllow;
	}
}
