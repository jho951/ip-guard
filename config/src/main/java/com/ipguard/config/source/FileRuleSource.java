package com.ipguard.config.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.ipguard.core.engine.RuleSource;
import com.ipguard.core.exception.ErrorCode;
import com.ipguard.core.exception.IpGuardException;
import com.ipguard.core.rules.IpRuleInterface;
import com.ipguard.core.rules.IpRuleParser;

/**
 * 단순 파일 기반 규칙 소스.
 *  - allow-ip.txt 같은 파일에서 규칙 문자열을 읽고
 *  - IpRuleParser로 파싱하여 List<IpRuleInterface>를 만든다.
 */
public final class FileRuleSource implements RuleSource {

	private final Path ruleFilePath;

	public Path getRuleFilePath() {
		return ruleFilePath;
	}

	public FileRuleSource(Path ruleFilePath) {
		if (ruleFilePath == null) {
			throw new IllegalArgumentException("ruleFilePath must not be null");
		}
		this.ruleFilePath = ruleFilePath;
	}

	@Override
	public List<IpRuleInterface> loadRules() {
		try {
			// 1) 파일 전체를 문자열로 읽기
			String raw = Files.readString(ruleFilePath);

			// 2) 파서로 규칙 리스트로 변환
			List<IpRuleInterface> rules = IpRuleParser.parse(raw);

			// 3) 방어적 복사 (외부에서 수정 못 하게)
			return List.copyOf(rules);
		} catch (IOException e) {
			// v1: 파일 읽기 실패는 IpGuardException으로 래핑
			throw new IpGuardException(
				ErrorCode.ERROR_RULE_FILE_IO,
				"규칙 파일을 읽을 수 없습니다: " + ruleFilePath,
				e
			);
		}
	}


}
