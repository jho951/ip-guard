package com.ipguard.core.rules;

import com.ipguard.core.ip.IpAddress;
import com.ipguard.core.ip.IpFamily;

public interface IpRule {
	IpFamily family();
	boolean matches(IpAddress ip);
}
