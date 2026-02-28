package com.ipguard.core.decision;

public final class Decision {
	private final boolean allowed;
	private final String reason;

	private Decision(boolean allowed, String reason) {
		this.allowed = allowed;
		this.reason = reason;
	}

	public static Decision allow(String reason) {
		return new Decision(true, reason == null ? "ALLOWED" : reason);
	}

	public static Decision deny(String reason) {
		return new Decision(false, reason == null ? "DENIED" : reason);
	}

	public boolean allowed() {
		return allowed;
	}

	public String reason() {
		return reason;
	}
}
