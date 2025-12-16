package com.ipguard.core.rules;

import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;
import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpParser;
import com.ipguard.core.ip.IpRange;

import java.util.ArrayList;
import java.util.List;

public final class RuleParser {

	private RuleParser() {}

	public static List<IpRule> parse(String raw) {
		if (raw == null || raw.isBlank()) return List.of();

		List<IpRule> out = new ArrayList<>();
		String[] lines = raw.split("\\R");

		for (String line : lines) {
			String s = stripComment(line).trim();
			if (s.isEmpty()) continue;
			out.add(parseOne(s));
		}
		return List.copyOf(out);
	}

	private static String stripComment(String line) {
		int hash = line.indexOf('#');
		int sl = line.indexOf("//");
		int cut = -1;
		if (hash >= 0) cut = hash;
		if (sl >= 0) cut = (cut < 0) ? sl : Math.min(cut, sl);
		return cut < 0 ? line : line.substring(0, cut);
	}

	private static IpRule parseOne(String s) {

		// ✅ IPv4 wildcard 지원: 192.168.*.* -> 192.168.0.0/16
		if (looksLikeIpv4Wildcard(s)) {
			String cidr = ipv4WildcardToCidr(s);
			int slash = cidr.indexOf('/');
			IpAddress ip = IpParser.parse(cidr.substring(0, slash).trim());
			int prefix = Integer.parseInt(cidr.substring(slash + 1).trim());
			return new CidrIpRule(ip.family(), prefix, ip.value128());
		}

		// CIDR
		int slash = s.indexOf('/');
		if (slash > 0) {
			IpAddress ip = IpParser.parse(s.substring(0, slash).trim());
			int prefix = Integer.parseInt(s.substring(slash + 1).trim());
			return new CidrIpRule(ip.family(), prefix, ip.value128());
		}

		// Range
		int dash = s.indexOf('-');
		if (dash > 0) {
			IpAddress start = IpParser.parse(s.substring(0, dash).trim());
			IpAddress end = IpParser.parse(s.substring(dash + 1).trim());

			if (start.family() != end.family()) {
				throw new IpGuardException(
					ErrorCode.RULE_FAMILY_MISMATCH,
					"range family mismatch: " + s
				);
			}

			return new RangeIpRule(
				new IpRange(start.family(), start.value128(), end.value128())
			);
		}

		// Single
		return new SingleIpRule(IpParser.parse(s));
	}

	private static boolean looksLikeIpv4Wildcard(String s) {
		if (s == null) return false;
		String t = s.trim();
		return t.matches("(?:(?:\\d{1,3}|\\*)\\.){3}(?:\\d{1,3}|\\*)")
			&& t.indexOf('*') >= 0;
	}

	private static String ipv4WildcardToCidr(String s) {
		String[] parts = s.split("\\.");
		if (parts.length != 4) throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "invalid wildcard: " + s);

		int fixed = 0;
		for (String p : parts) {
			if ("*".equals(p)) break;

			int v;
			try { v = Integer.parseInt(p); }
			catch (Exception e) { throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "invalid wildcard: " + s); }
			if (v < 0 || v > 255) throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "invalid wildcard: " + s);

			fixed++;
		}

		// ✅ "*"는 연속이어야 함 (10.*.1.* 같은 형태 금지)
		for (int i = fixed; i < 4; i++) {
			if (!"*".equals(parts[i])) {
				throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "wildcard must be contiguous: " + s);
			}
		}

		if (fixed == 0) throw new IpGuardException(ErrorCode.INVALID_IP_ADDRESS, "too broad wildcard: " + s);

		int prefix = fixed * 8;

		StringBuilder ip = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i > 0) ip.append('.');
			if (i < fixed) ip.append(parts[i]);
			else ip.append('0');
		}
		return ip + "/" + prefix;
	}
}
