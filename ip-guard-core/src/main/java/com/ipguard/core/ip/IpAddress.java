package com.ipguard.core.ip;

import java.math.BigInteger;

public interface IpAddress extends Comparable<IpAddress> {
	IpFamily family();
	BigInteger value128();
	String normalized();
}
