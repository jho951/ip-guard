package com.ipguard.file.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.ipguard.core.engine.RuleSource;
import com.ipguard.core.exception.IpGuardErrorCode;
import com.ipguard.core.exception.IpGuardException;
import com.ipguard.core.rules.IpRuleInterface;
import com.ipguard.core.rules.IpRuleParser;

/**
 * 단순 파일 기반 규칙 소스 (v1).
 *  - allow-ip.txt 같은 파일에서 규칙 문자열을 읽고
 *  - IpRuleParser로 파싱하여 List<IpRuleInterface>를 만든다.
 *  - v1: 리로드 없음 (엔진 생성 시 한 번만 호출)
 */
public final class FileRuleSource implements RuleSource {

	private final Path ruleFilePath;

	public FileRuleSource(Path ruleFilePath) {
		this.ruleFilePath = ruleFilePath;
	}

	@Override
	public List<IpRuleInterface> loadRules() {
		try {
			String raw = Files.readString(ruleFilePath);
			List<IpRuleInterface> rules = IpRuleParser.parse(raw);

			return List.copyOf(rules);

		} catch (IOException e) {
			throw new IpGuardException(
				IpGuardErrorCode.ERROR_RULE_FILE_IO,
				"파일을 읽을 수 없습니다.: " + ruleFilePath,
				e
			);
		}
	}
}
