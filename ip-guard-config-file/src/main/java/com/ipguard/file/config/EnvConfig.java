package com.ipguard.file.config;

/**
 * IP 화이트리스트용 환경설정 유틸.
 *
 * <ul>
 *   <li>환경변수에서 문자열 값을 읽고</li>
 *   <li>기본 IP 규칙(DEFAULT_IP)을 제공하며</li>
 *   <li>규칙 문자열의 구분자(~)를 정규화한다.</li>
 *   <li>allow-ip 파일명/디렉터리 힌트, 리로드 주기 등을 제공한다.</li>
 * </ul>
 *
 * <b>주의:</b> 이 클래스는 OS, 파일 시스템, Desktop 경로 등을 전혀 다루지 않는다.
 * 실제 파일 읽기/쓰기, 경로 해석은 별도의 모듈에서 처리해야 한다.
 */
public final class EnvConfig {

	private EnvConfig() {}

	/**
	 * 환경변수 값을 읽어 trim 처리 후 반환한다.
	 *
	 * @param envKey 환경변수 키
	 * @return 값이 없거나 빈 문자열이면 null, 아니면 trim된 값
	 */
	private static String env(String envKey) {
		if (envKey == null || envKey.isEmpty()) return null;
		String envValue = System.getenv(envKey);
		if (envValue == null ||  envValue.isEmpty()) return null;
		return envValue.trim();
	}

	private static String envOrDefault(String envKey, String defaultValue) {
		String envValue = env(envKey);
		if (envValue == null || envValue.isEmpty()) return defaultValue;
		return envValue;
	}

	/**
	 * IP 규칙 문자열에서 범위 구분자 "~"를 "-"로 정규화한다.
	 *
	 * <pre>
	 * "10.0.0.1 ~ 10.0.0.10" → "10.0.0.1-10.0.0.10"
	 * </pre>
	 *
	 * @param ipRules IP 규칙 원본 문자열
	 * @return null/blank면 그대로, 아니면 "~"→"-" 치환 후 trim
	 */
	public static String normalizeRules(String ipRules) {
		if (ipRules == null || ipRules.isBlank()) return ipRules;
		return ipRules.replaceAll("\\s*~\\s*", "-").trim();
	}

	/**
	 * DEFAULT_IP 규칙을 정규화한 값.
	 *
	 * @return "~"가 "-"로 치환된 DEFAULT_IP 규칙 문자열
	 */
	public static String defaultIpRules() {
		return normalizeRules("DEFAULT_IP");
	}
}

