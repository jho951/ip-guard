# Development Guide

`ip-guard` 프로젝트의 개발/확장/릴리즈를 위한 문서입니다.

## 1. 개발 환경
- JDK 17
- Gradle Wrapper (`./gradlew`)
- 권장 IDE: IntelliJ IDEA

확인 명령:

```bash
java -version
./gradlew -v
```

## 2. 프로젝트 구조

```text
ip-guard/
├─ core/         # 엔진, 파서, 규칙 매칭 로직
├─ spi/          # RuleSource SPI
├─ source-env/   # 환경변수 기반 RuleSource
├─ source-file/  # 파일 기반 RuleSource
├─ config/       # Spring Boot 자동설정 + 필터
├─ build.gradle  # 공통 그룹/버전/퍼블리싱 설정
└─ settings.gradle
```

### 모듈 의존
- `core` -> `spi`
- `source-env` -> `spi`
- `source-file` -> `spi`
- `config` -> `core`, `spi`, `source-env`

## 3. 빌드/테스트

전체 빌드:

```bash
./gradlew clean build
```

전체 테스트:

```bash
./gradlew test
```

모듈 단위 테스트:

```bash
./gradlew :core:test
./gradlew :source-file:test
```

## 4. 핵심 도메인 설명

### 4.1 RuleSource (SPI)
파일: `spi/src/main/java/com/ipguard/spi/RuleSource.java`

- 역할: 규칙 원문 문자열 제공
- 메서드: `String loadRaw()`
- 원칙: `null` 대신 빈 문자열 반환 권장

### 4.2 엔진
파일: `core/src/main/java/com/ipguard/core/engine/IpGuardEngine.java`

동작 흐름:
1. `RuleSource#loadRaw()` 호출
2. `RuleParser.parse(raw)`로 `IpRule` 목록 생성
3. `RuleSet`에 저장
4. `decide(rawIp)` 호출 시 파싱/매칭 후 `Decision` 반환

정책:
- 잘못된 IP 입력: `INVALID_IP`로 거부
- 규칙 비어 있음:
  - `defaultAllow=true` -> 허용 (`DEFAULT_ALLOW`)
  - `defaultAllow=false` -> 거부 (`DEFAULT_DENY`)
- 규칙 존재:
  - 매칭 -> `MATCHED_RULE`
  - 미매칭 -> `NO_MATCH`

### 4.3 룰 파서
파일: `core/src/main/java/com/ipguard/core/rules/RuleParser.java`

지원 문법:
- Single: `127.0.0.1`
- CIDR: `10.0.0.0/8`, `2001:db8::/32`
- Range: `192.168.1.10-192.168.1.20`
- IPv4 wildcard: `192.168.*.*`

세부 규칙:
- 빈 줄/주석(`#`, `//`) 무시
- 와일드카드는 연속형만 허용 (`10.*.*.*` 가능, `10.*.1.*` 불가)
- `*.*.*.*`는 너무 넓은 규칙으로 예외

### 4.4 Spring Boot 통합
파일:
- `config/src/main/java/com/ipguard/config/IpGuardAutoConfiguration.java`
- `config/src/main/java/com/ipguard/config/IpGuardFilter.java`
- `config/src/main/java/com/ipguard/config/IpGuardProperties.java`

자동 등록 Bean:
- `RuleSource` (기본: `EnvRuleSource`)
- `IpGuardEngine`
- `IpGuardFilter`

`@ConditionalOnMissingBean`이 적용되어 있으므로, 사용자가 동일 타입 Bean을 등록하면 기본 Bean 대신 사용자 Bean이 사용됩니다.

## 5. 새 RuleSource 추가 방법
예: DB/Redis/Config Server 기반 소스

1. 새 모듈 생성 (예: `source-db`)
2. `RuleSource` 구현
3. 실패 시 동작 정책 결정
   - 현재 구현들은 실패 시 빈 문자열로 폴백
4. 필요하면 `config`에서 기본 `RuleSource` 교체 가능

예시 스켈레톤:

```java
public final class DbRuleSource implements RuleSource {
    @Override
    public String loadRaw() {
        // DB에서 규칙 문자열 조회
        return "127.0.0.1\n10.0.0.0/8";
    }
}
```

## 6. 릴리즈/퍼블리싱
루트 `build.gradle` 기준:
- `group = io.github.jho951`
- `version = 2.0.3`
- artifactId 패턴: `ip-guard-{module}`

퍼블리시 대상:
- GitHub Packages (`https://maven.pkg.github.com/jho951/ip-guard`)

필요 프로퍼티:
- `githubUsername`
- `githubToken`

예시:

```bash
./gradlew publish \
  -PgithubUsername=<username> \
  -PgithubToken=<token>
```

## 7. 코딩/테스트 가이드
- Java 17 기준 코드 작성
- 파서/엔진 변경 시 `core` 테스트 추가
- 새 소스 모듈 추가 시 `RuleSource` 실패/빈값/정상 케이스 테스트 포함
- 필터 동작 변경 시 상태 코드/응답 포맷/헤더 처리 회귀 확인

## 8. 트러블슈팅
- 규칙이 적용되지 않을 때:
  - `ipguard.env-key` 값 확인
  - 실제 환경변수 존재 여부 확인
  - 규칙 문법(CIDR prefix, range 구분자, wildcard 형태) 확인
- 프록시 뒤에서 오탐 차단 시:
  - `X-Forwarded-For` 전달 및 신뢰 체인 설정 확인
