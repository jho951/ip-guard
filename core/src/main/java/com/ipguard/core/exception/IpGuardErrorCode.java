package com.ipguard.core.exception;

/**
 * Ip-guard 모듈 전용 에러 코드
 * IP로 코드를 적용
 */
public enum IpGuardErrorCode {

	// -----------------------------
	// 설정/환경 관련
	// -----------------------------
	REQUIRED_ENV_MISSING("IP001", "ENV 값이 없습니다."),
	INVALID_ENV_VALUE("IP002", "ENV 값이 일치하지 않습니다."),
	// -----------------------------
	// 규칙/파싱 관련
	// -----------------------------
	INVALID_IP_ADDRESS("IP003", "IPv4 주소가 일치하지 않습니다."),
	INVALID_RULE_SYNTAX("IP004", "IP 규칙이 일치하지 않습니다."),
	UNSUPPORTED_RULE_TYPE("IP005", "지원하지 않는 IP 규칙입니다."),
	UNSUPPORTED_IP_TYPE("IP006","지원하지 않는 IP 형식입니다."),
	NOT_FOUND_IP("IP007","IP를 찾을 수 없습니다."),
	// -----------------------------
	// 엔진/로딩 관련
	// -----------------------------
	RULE_LOADING_FAILED("IP008", "IP 규칙을 읽지 못했습니다."),
	ENGINE_INIT_FAILED("IP009", "IpGuard engine을 읽지 못했습니다."),
	// -----------------------------
	// 파일
	// -----------------------------
	ERROR_RULE_FILE_IO("IP010","파일을 읽지 못했습니다.");
	// -----------------------------
	// fields
	// -----------------------------
	private final String code;
	private final String defaultMessage;

	IpGuardErrorCode(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	/**
	 * "E001", "E010" 처럼 심볼릭 코드.
	 */
	public String code() {
		return code;
	}

	/**
	 * 기본 메시지. 로그용/디폴트 응답용으로 사용.
	 */
	public String defaultMessage() {
		return defaultMessage;
	}

	@Override
	public String toString() {
		return code + " - " + defaultMessage;
	}
}
