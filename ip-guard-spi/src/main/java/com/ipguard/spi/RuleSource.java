package com.ipguard.spi;

/**
 * Core가 사용하는 규칙 원문 공급 인터페이스.
 * 구현은 규칙 텍스트만 반환하고, 파싱과 판정은 core가 담당한다.
 */
public interface RuleSource {
	/** @return 여러 줄 규칙 원문. null이면 빈 문자열로 처리 권장 */
	String loadRaw();
}
