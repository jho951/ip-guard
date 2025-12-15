package com.ipguard.config;

/**
 * ENV 기반 설정 유틸
 * 특정 환경변수에서 규칙 문자열을 읽고 "~" 같은 기호를 정규화
 */
public final class EnvConfig {

	private EnvConfig() {}

	/**
	 * raw 환경 변수 값 그대로 읽기
	 */
	private static String rawEnv(String envName) {
		return System.getenv(envName);
	}

	/**
	 * 규칙 문자열 정규화:
	 * - null이면 null 유지
	 * - 앞뒤 공백 제거
	 * - " ~ " → "-" 로 치환 (IP 범위 표기 통일)
	 */
	private static String normalizeRules(String rawRules) {
		if (rawRules == null) {return null;}
		String trimmed = rawRules.trim();
		if (trimmed.isEmpty()) {return trimmed;}
		return trimmed.replaceAll("\\s*~\\s*", "-");
	}

	/**
	 * 주어진 환경변수에서 읽고, 정규화까지 해서 반환.
	 * 없으면 null.
	 */
	public static String normalizedEnvRules(String envName) {
		String raw = rawEnv(envName);
		return normalizeRules(raw);
	}
}
