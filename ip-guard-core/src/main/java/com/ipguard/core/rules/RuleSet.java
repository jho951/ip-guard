package com.ipguard.core.rules;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;

import java.util.ArrayList;
import java.util.List;

public final class RuleSet {

	private final List<IpRule> v4;
	private final List<IpRule> v6;

	public RuleSet(List<IpRule> rules) {
		List<IpRule> a = new ArrayList<>();
		List<IpRule> b = new ArrayList<>();

		if (rules != null) {
			for (IpRule r : rules) {
				if (r == null) continue;
				if (r.family() == IpFamily.IPV4) a.add(r);
				else b.add(r);
			}
		}

		this.v4 = List.copyOf(a);
		this.v6 = List.copyOf(b);
	}

	public boolean isEmpty() {
		return v4.isEmpty() && v6.isEmpty();
	}

	public boolean anyMatch(IpAddress ip) {
		if (ip == null) return false;

		List<IpRule> list =
			(ip.family() == IpFamily.IPV4) ? v4 : v6;

		for (IpRule r : list) {
			if (r.matches(ip)) return true;
		}
		return false;
	}
}
