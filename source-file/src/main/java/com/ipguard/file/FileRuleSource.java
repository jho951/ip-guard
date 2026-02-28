package com.ipguard.file;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.ipguard.spi.RuleSource;

public final class FileRuleSource implements RuleSource {

	private final Path path;

	public FileRuleSource(Path path) {
		this.path = path;
	}

	@Override
	public String loadRaw() {
		try {
			if (path == null) return "";
			if (!Files.exists(path)) return "";
			return Files.readString(path, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return "";
		}
	}
}
