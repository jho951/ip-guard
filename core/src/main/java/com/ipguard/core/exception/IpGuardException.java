package com.ipguard.core.exception;

public class IpGuardException extends RuntimeException {

	private final IpGuardErrorCode errorCode;

	public IpGuardException(IpGuardErrorCode errorCode) {
		super(errorCode.defaultMessage());
		this.errorCode = errorCode;
	}

	public IpGuardException(IpGuardErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public IpGuardException(IpGuardErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public IpGuardException(IpGuardErrorCode errorCode, Throwable cause) {
		super(errorCode.defaultMessage(), cause);
		this.errorCode = errorCode;
	}

	public IpGuardErrorCode getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		return "IpGuardException{" + "errorCode=" + errorCode.code() + ", message=" + getMessage() + '}';
	}
}
