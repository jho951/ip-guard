package com.ipguard.core.decision;

/** IP 통괴 여부 판단 */
public final class Decision {
	/** 통과 여부 (true | false) */
	private final boolean allowed;
	/**  이런 결정을 내렸는지 적어두는 메모 */
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

	public boolean allowed() {return allowed;}
	public String reason() {return reason;}
}
