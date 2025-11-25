package com.ipguard.rules;

import com.ipguard.util.IpUtils;

/**
 * 와일드카드 IP 규칙.
 *
 * <p>예)</p>
 * <ul>
 *   <li>192.168.*.*</li>
 *   <li>10.*</li>
 *   <li>*.*.*.*</li>
 * </ul>
 *
 * <p>각 옥텟이 '*'이면 해당 옥텟을 무시하고, 숫자면 고정 매칭한다.</p>
 */
public final class WildcardIpRule implements IpRule {

	private final String raw;
	private final int baseInt;
	private final int maskInt;

	/**
	 * @param wildcardRaw 와일드카드 규칙 문자열
	 * @throws IllegalArgumentException 형식이 잘못된 경우
	 */
	public WildcardIpRule(String wildcardRaw) {
		if (wildcardRaw == null || wildcardRaw.isBlank()) {
			throw new IllegalArgumentException("Wildcard rule is blank.");
		}

		this.raw = wildcardRaw.trim();
		String[] parts = this.raw.split("\\.");

		if (parts.length < 1 || parts.length > 4) {
			throw new IllegalArgumentException("Invalid wildcard rule: " + raw);
		}

		int base = 0;
		int mask = 0;

		// parts가 4개가 아니면 뒤를 '*'로 채움 (예: "10.*" → "10.*.*.*")
		String[] octets = new String[4];
		for (int i = 0; i < 4; i++) {
			octets[i] = (i < parts.length) ? parts[i].trim() : "*";
		}

		for (int i = 0; i < 4; i++) {
			String o = octets[i];

			base <<= 8;
			mask <<= 8;

			if ("*".equals(o)) {
				// wildcard 옥텟: mask는 0x00 → 비교에서 무시됨
				base |= 0x00;
				mask |= 0x00;
			} else {
				int v;
				try {
					v = Integer.parseInt(o);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid octet in wildcard rule: " + raw);
				}

				if (v < 0 || v > 255) {
					throw new IllegalArgumentException("Octet out of range in wildcard rule: " + raw);
				}

				base |= (v & 0xFF);
				mask |= 0xFF; // 숫자 옥텟은 고정 비교
			}
		}

		this.baseInt = base;
		this.maskInt = mask;
	}

	@Override
	public IpRuleType type() {
		return IpRuleType.WILDCARD;
	}

	@Override
	public boolean matches(String clientIp) {
		int c = IpUtils.ipToInt(clientIp);
		return (c & maskInt) == (baseInt & maskInt);
	}

	@Override
	public String raw() {
		return raw;
	}
}
