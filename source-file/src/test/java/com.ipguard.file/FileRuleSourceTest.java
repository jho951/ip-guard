package com.ipguard.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ipguard.spi.RuleSource;

@DisplayName("FileRuleSource 클래스 테스트")
class FileRuleSourceTest {

	// JUnit 5의 @TempDir을 사용하여 테스트 실행 중 임시 디렉토리를 생성하고,
	// 테스트 종료 후 자동으로 정리되도록 합니다.
	@TempDir
	Path tempDir;

	private Path testFilePath;
	private final String fileContent = "This is a test rule content.";
	private final String emptyContent = "";

	@BeforeEach
	void setup() throws IOException {
		// 모든 테스트 전에 임시 파일 생성
		testFilePath = tempDir.resolve("test-rules.txt");
		Files.write(testFilePath, fileContent.getBytes(StandardCharsets.UTF_8));
	}

	@AfterEach
	void tearDown() throws IOException {
		// @TempDir이 자동으로 정리하겠지만, 명시적으로 삭제 로직을 추가할 수도 있습니다.
		// 현재는 @TempDir 사용으로 생략합니다.
	}

	@Test
	@DisplayName("파일이 존재할 때 내용을 성공적으로 로드해야 한다")
	void testLoadRaw_FileExists() {
		// Given
		RuleSource source = new FileRuleSource(testFilePath);

		// When
		String actualContent = source.loadRaw();

		// Then
		assertNotNull(actualContent);
		assertEquals(fileContent, actualContent, "로드된 내용이 파일 내용과 일치해야 합니다.");
	}

	@Test
	@DisplayName("파일이 존재하지 않을 때 빈 문자열을 반환해야 한다")
	void testLoadRaw_FileDoesNotExist() {
		// Given
		Path nonExistentPath = tempDir.resolve("non-existent.txt");
		RuleSource source = new FileRuleSource(nonExistentPath);

		// When
		String actualContent = source.loadRaw();

		// Then
		assertNotNull(actualContent);
		assertEquals(emptyContent, actualContent, "파일이 없을 때 빈 문자열이 반환되어야 합니다.");
	}

	@Test
	@DisplayName("파일 경로가 null일 때 빈 문자열을 반환해야 한다")
	void testLoadRaw_PathIsNull() {
		// Given
		RuleSource source = new FileRuleSource(null);

		// When
		String actualContent = source.loadRaw();

		// Then
		assertNotNull(actualContent);
		assertEquals(emptyContent, actualContent, "경로가 null일 때 빈 문자열이 반환되어야 합니다.");
	}

	@Test
	@DisplayName("빈 파일의 내용을 로드할 때 빈 문자열을 반환해야 한다")
	void testLoadRaw_EmptyFile() throws IOException {
		// Given: 빈 파일 생성
		Path emptyFilePath = tempDir.resolve("empty-rules.txt");
		Files.write(emptyFilePath, "".getBytes(StandardCharsets.UTF_8));
		RuleSource source = new FileRuleSource(emptyFilePath);

		// When
		String actualContent = source.loadRaw();

		// Then
		assertNotNull(actualContent);
		assertEquals(emptyContent, actualContent, "빈 파일을 로드할 때 빈 문자열이 반환되어야 합니다.");
	}
}