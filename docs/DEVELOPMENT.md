# Development Guide

`ip-guard` 프로젝트의 개발/확장/릴리즈를 위한 문서입니다.

이 저장소는 1계층 OSS를 대상으로 합니다. 따라서 여기에 들어가는 코드는
특정 서비스명, 특정 도메인 모델, 특정 배포 구조, 특정 플랫폼 정책에 의존하면 안 됩니다.

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
├─ source-env/   # 환경변수 기반 RuleSource + auto-config
├─ source-file/  # 파일 기반 RuleSource + auto-config
├─ config/       # Spring Boot 스타터(자동설정 + 필터)
├─ build.gradle  # 공통 그룹/버전/퍼블리싱 설정
└─ settings.gradle
```

### 모듈 의존
- `core` -> `spi`
- `source-env` -> `spi`
- `source-file` -> `spi`
- `source-env` -> spring boot autoconfigure
- `source-file` -> spring boot autoconfigure
- `config` -> `core`, `spi`

아티팩트 매핑:
- `spi` -> `ip-guard-spi`
- `core` -> `ip-guard-core`
- `source-env` -> `ip-guard-source-env`
- `source-file` -> `ip-guard-source-file`
- `config` -> `ip-guard-spring-boot-starter`

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

### 4.4 IP 파서 정규화
파일: `core/src/main/java/com/ipguard/core/ip/IpParser.java`

`IpGuardEngine#decide(rawIp)`에 전달된 입력은 파싱 전에 아래 규칙으로 정규화됩니다.
- 양끝 공백 제거
- `"..."`로 감싼 입력이면 따옴표 제거
- `[IPv6]:port` 형태면 브래킷 내부 주소만 추출
- `IPv4:port` 형태면 포트 제거
- IPv6 zone(`%en0` 등) 제거

예:
- `[2001:db8::1]:443` -> `2001:db8::1`
- `192.168.0.10:8080` -> `192.168.0.10`
- `fe80::1%en0` -> `fe80::1`

### 4.5 Spring Boot 통합
파일:
- `config/src/main/java/com/ipguard/config/IpGuardAutoConfiguration.java`
- `config/src/main/java/com/ipguard/config/IpGuardFilter.java`
- `config/src/main/java/com/ipguard/config/IpGuardProperties.java`
- `config/src/main/java/com/ipguard/config/ClientIpResolver.java`
- `config/src/main/java/com/ipguard/config/BlockResponseWriter.java`
- `source-env/src/main/java/com/ipguard/env/EnvRuleSourceAutoConfiguration.java`
- `source-env/src/main/java/com/ipguard/env/EnvRuleSourceProperties.java`
- `source-file/src/main/java/com/ipguard/file/FileRuleSourceAutoConfiguration.java`
- `source-file/src/main/java/com/ipguard/file/FileRuleSourceProperties.java`

자동 등록 Bean:
- `IpGuardEngine`
- `IpGuardFilter`
- `RuleSource` (기본 제공은 `source-env` 모듈)
- `ClientIpResolver`
- `BlockResponseWriter`

`@ConditionalOnMissingBean`이 적용되어 있으므로, 사용자가 동일 타입 Bean을 등록하면 기본 Bean 대신 사용자 Bean이 사용됩니다.

필터 응답은 최소한의 공통 형식을 제공하되 `BlockResponseWriter`로 교체 가능해야 하며, gateway/auth/user/block 같은 상위 서비스 규약을 직접 강제하지 않습니다.

`config`는 범용 starter 조립만 담당하고, 환경변수 기반 기본 `RuleSource`는 `source-env` 모듈이 담당합니다.
`source-file`는 `ipguard.file.path`가 설정된 경우에만 파일 기반 `RuleSource`를 제공합니다.
`config`의 클라이언트 IP 추출은 `ClientIpResolver`로 분리되어 있으며, `REMOTE_ADDR_ONLY`, `XFF_FIRST`, `TRUSTED_PROXY_CHAIN` 전략을 선택할 수 있습니다.

## 5. 새 RuleSource 추가 방법
예: DB/Redis/Config Server 기반 소스

1. 새 모듈 생성 (예: `source-db`)
2. `RuleSource` 구현
3. 실패 시 동작 정책 결정
   - 현재 구현들은 실패 시 빈 문자열로 폴백
4. 필요하면 `source-env` 또는 `source-file` auto-config, 혹은 애플리케이션 레벨에서 기본 `RuleSource` 교체 가능

주의:
- 여기서 만드는 source는 범용 RuleSource여야 합니다.
- 특정 서비스 전용 키 설계나 도메인 규칙을 내장하지 마세요.
- 플랫폼 전용 조합 규칙은 2계층에서 다루는 것이 맞습니다.

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
- `version = 2.0.5`
- artifactId:
  - `ip-guard-spi`
  - `ip-guard-core`
  - `ip-guard-source-env`
  - `ip-guard-source-file`
  - `ip-guard-spring-boot-starter`

퍼블리시 대상:
- GitHub Packages (`https://maven.pkg.github.com/jho951/ip-guard`)
- Sonatype Central

GitHub Packages 필요 프로퍼티:
- `githubUsername`
- `githubToken`

실행 예시:

```bash
./gradlew publishToGitHubPackages \
  -PgithubUsername=<username> \
  -PgithubToken=<token>
```

Sonatype Central 필요 프로퍼티:
- `mavenCentralUsername`
- `mavenCentralPassword`
- `mavenCentralSigningKey` (ASCII-armored private key 또는 Base64/escaped newline 문자열)
- `mavenCentralSigningPassword`
- `centralUsername`
- `centralPassword`
- `signingKey` (ASCII-armored private key)
- `signingPassword`
- `ossrhUsername`
- `ossrhPassword`

GitHub Actions 시크릿으로는 아래 이름도 지원합니다:
- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_PASSWORD`
- `MAVEN_CENTRAL_GPG_PRIVATE_KEY`
- `MAVEN_CENTRAL_GPG_PASSPHRASE`
- `MAVEN_CENTRAL_RELEASE_URL`
- `MAVEN_CENTRAL_SNAPSHOT_URL`

주의:
- `mavenCentralUsername` / `mavenCentralPassword` 또는 `centralUsername` / `centralPassword`에는 Sonatype Central Portal의 user token 값을 사용합니다.
- `MAVEN_CENTRAL_GPG_PRIVATE_KEY`는 ASCII-armored key 원문, `\n`이 포함된 문자열, Base64 인코딩 문자열을 모두 허용합니다.
- GitHub Actions Central 배포는 `publish`가 아니라 `publishToSonatypeCentral` task를 사용해야 합니다. 그렇지 않으면 GitHub Packages 퍼블리시까지 같이 시도합니다.

실행 예시:

```bash
./gradlew publishToSonatypeCentral \
  -PmavenCentralUsername=<central-portal-username> \
  -PmavenCentralPassword=<central-portal-password> \
  -PmavenCentralSigningKey="$(cat ./private.asc)" \
  -PmavenCentralSigningPassword=<gpg-passphrase>
```

GitHub Actions:
- `.github/workflows/publish.yml`: Sonatype Central 배포(tag push 또는 수동 실행)

## 7. 코딩/테스트 가이드
- Java 17 기준 코드 작성
- 파서/엔진 변경 시 `core` 테스트 추가
- 새 소스 모듈 추가 시 `RuleSource` 실패/빈값/정상 케이스 테스트 포함
- 필터 동작 변경 시 상태 코드/응답 포맷/헤더 처리 회귀 확인

## 8. 트러블슈팅
- 규칙이 적용되지 않을 때:
  - `ipguard.env.env-key` 값 확인
  - 실제 환경변수 존재 여부 확인
  - 규칙 문법(CIDR prefix, range 구분자, wildcard 형태) 확인
- 프록시 뒤에서 오탐 차단 시:
  - `ipguard.client-ip-strategy` 설정 확인
  - `trusted-proxies` 설정 확인
