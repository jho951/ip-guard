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
	 * "~" 등을 정규화해서, 파서가 먹기 좋은 형태로 만들어 준다.
	 * 예) "192.168.0.1 ~ 192.168.0.10" -> "192.168.0.1-192.168.0.10"
	 */
	public static String normalizeRules(String rawRules) {
		if (rawRules == null) {
			return null;
		}
		String trimmed = rawRules.trim();
		if (trimmed.isEmpty()) {
			return trimmed;
		}
		return trimmed.replaceAll("\\s*~\\s*", "-");
	}

	/**
	 * DEFAULT_IP 환경변수를 읽어 정규화한 문자열 반환.
	 * 없으면 null.
	 */
	public static String defaultIpRules() {
		String raw = rawDefaultIpRules();
		return normalizeRules(raw);
	}

	/**
	 * 환경변수에서 원시 규칙 문자열을 읽는다.
	 * 없으면 null.
	 */
	public static String rawDefaultIpRules() {
		return System.getenv("DEFAULT_IP");
	}

	/**
	 * 규칙 파일 경로를 나타내는 환경변수.
	 * 예) ALLOW_IP_PATH=/etc/ip-guard/allow-ip.txt
	 */
	public static String ruleFilePathFromEnv() {
		return System.getenv("ALLOW_IP_PATH");
	}
}

