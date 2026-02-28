package com.ipguard.spi;

/**
 * 규칙 원천(SPI)
 * - source-env/source-file 같은 모듈이 구현
 * - v2에서는 "raw text"만 제공하고 파싱/룰 생성은 core가 담당
 */
public interface RuleSource {
	/**
	 * @return 여러 줄 규칙 원문. null이면 빈 문자열로 처리 권장
	 */
	String loadRaw();
}