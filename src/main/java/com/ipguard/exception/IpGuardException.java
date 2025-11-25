package com.ipguard.exception;

/**
 * Ip-guard 전용 런타임 예외.
 *
 * <p>
 * - ErrorCode를 반드시 포함한다.
 * - 메시지는 ErrorCode의 기본 메시지 또는 상세 메시지를 함께 사용할 수 있다.
 * </p>
 */
public class IpGuardException extends RuntimeException {

	private final IpGuardErrorCode errorCode;

	/**
	 * 기본 메시지( ErrorCode.defaultMessage )로 예외 생성.
	 */
	public IpGuardException(IpGuardErrorCode errorCode) {
		super(errorCode.defaultMessage());
		this.errorCode = errorCode;
	}

	/**
	 * 상세 메시지를 함께 지정하는 생성자.
	 */
	public IpGuardException(IpGuardErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * 원인(cause)까지 포함하는 생성자.
	 */
	public IpGuardException(IpGuardErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	/**
	 * 기본 메시지 + cause.
	 */
	public IpGuardException(IpGuardErrorCode errorCode, Throwable cause) {
		super(errorCode.defaultMessage(), cause);
		this.errorCode = errorCode;
	}

	public IpGuardErrorCode getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		// 로그에 찍힐 때 코드까지 같이 보이도록
		return "IpGuardException{" +
			"errorCode=" + errorCode.code() +
			", message=" + getMessage() +
			'}';
	}
}
