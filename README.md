# ip-guard

경량 IP 접근 제어 라이브러리입니다. 고정된 화이트리스트 규칙을 기반으로 요청 IP를 허용/차단합니다.

현재 저장소는 멀티 모듈 Gradle 프로젝트이며, IPv4/IPv6 파싱과 규칙 매칭 엔진, 규칙 소스(SPI), Spring Boot 자동 설정을 제공합니다.

## 핵심 기능
- 단일 IP, CIDR, 범위(`start-end`), IPv4 와일드카드(`192.168.*.*`) 규칙 지원
- IPv4/IPv6 동시 지원
- `RuleSource` SPI 기반 규칙 로딩 분리
- Spring Boot AutoConfiguration + `OncePerRequestFilter` 제공
- 규칙이 비어 있을 때 `defaultAllow` 정책 지원

## 모듈 구성
- `spi`: 규칙 소스 인터페이스 (`RuleSource`)
- `core`: IP 파싱, 룰 파싱/매칭, 결정 엔진 (`IpGuardEngine`)
- `source-env`: 환경변수 기반 `RuleSource` 구현
- `source-file`: 파일 기반 `RuleSource` 구현
- `config`: Spring Boot 자동 설정 + HTTP 필터

## 빠른 시작

### 1) Spring Boot에서 사용
`config` 모듈은 자동으로 다음 Bean을 구성합니다.
- `RuleSource` (기본: `EnvRuleSource`)
- `IpGuardEngine`
- `IpGuardFilter`

예시 의존성:

```gradle
dependencies {
    implementation "io.github.jho951:ip-guard-config:2.0.3"
}
```

설정(`application.yml`):

```yaml
ipguard:
  default-allow: false
  env-key: IPGUARD_RULES
```

환경변수 예시:

```bash
export IPGUARD_RULES=$'127.0.0.1\n10.0.0.0/8\n192.168.1.10-192.168.1.20'
```

서버 기동 후 요청 IP가 룰에 매칭되지 않으면 `403`과 함께 아래 형태의 응답을 반환합니다.

```json
{"message":"IP_BLOCKED","reason":"NO_MATCH"}
```

### 2) 코어 엔진만 직접 사용

```java
RuleSource source = () -> "127.0.0.1\n10.10.*.*";
IpGuardEngine engine = new IpGuardEngine(source, false);

Decision decision = engine.decide("10.10.1.2");
boolean allowed = decision.allowed();
String reason = decision.reason();
```

## 규칙 포맷
한 줄에 하나의 규칙을 작성합니다.

- 단일 IP
  - `127.0.0.1`
  - `2001:db8::1`
- CIDR
  - `10.0.0.0/8`
  - `2001:db8::/32`
- 범위
  - `192.168.1.10-192.168.1.20`
- IPv4 와일드카드
  - `10.*.*.*` -> `10.0.0.0/8`
  - `192.168.*.*` -> `192.168.0.0/16`

주석/공백 처리:
- 빈 줄은 무시됩니다.
- `#`, `//` 이후는 주석으로 처리됩니다.

주의:
- 와일드카드는 연속되어야 합니다. (`10.*.1.*` 불가)
- `*.*.*.*` 같은 전체 허용 형태는 허용되지 않습니다.
- 범위 시작/끝 IP의 family(IPv4/IPv6)가 다르면 예외가 발생합니다.

## 판정 결과(reason)
`IpGuardEngine#decide`의 대표 reason 값:
- `MATCHED_RULE`: 규칙 매칭으로 허용
- `NO_MATCH`: 규칙은 있으나 미매칭으로 차단
- `DEFAULT_ALLOW`: 규칙 비어 있고 기본 허용
- `DEFAULT_DENY`: 규칙 비어 있고 기본 차단
- `INVALID_IP`: 입력 IP 파싱 실패로 차단

## Spring 필터 동작
`IpGuardFilter`는 클라이언트 IP를 다음 순서로 추출합니다.
1. `X-Forwarded-For` 첫 번째 값
2. `HttpServletRequest#getRemoteAddr()`

프록시 환경에서는 `X-Forwarded-For`를 신뢰할 수 있도록 인프라(로드밸런서/리버스프록시) 설정을 먼저 맞추는 것을 권장합니다.

## 커스텀 RuleSource 사용
기본 `EnvRuleSource` 대신, `RuleSource` Bean을 직접 등록하면 자동 설정이 이를 사용합니다.

```java
@Bean
RuleSource ruleSource() {
    return new FileRuleSource(Path.of("./ip-rules.txt"));
}
```

## 로컬 빌드/테스트

```bash
./gradlew clean build
./gradlew test
```

## 개발 문서
개발/기여를 위한 상세 내용은 `docs/DEVELOPMENT.md`를 참고하세요.
