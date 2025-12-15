package com.ipguard.core.exception;

public class IpGuardException extends RuntimeException {

	private final ErrorCode errorCode;

	public IpGuardException(ErrorCode errorCode) {
		super(errorCode.defaultMessage());
		this.errorCode = errorCode;
	}

	public IpGuardException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public IpGuardException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public IpGuardException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.defaultMessage(), cause);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		return "IpGuardException{" + "errorCode=" + errorCode.code() + ", message=" + getMessage() + '}';
	}
}
