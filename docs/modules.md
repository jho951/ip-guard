# Modules

## 모듈 목록

| Module | Responsibility | Artifact |
| --- | --- | --- |
| `ip-guard-spi` | SPI 인터페이스 | `io.github.jho951:ip-guard-spi` |
| `ip-guard-core` | IP 파싱, 룰 파싱/매칭, 결정 엔진 | `io.github.jho951:ip-guard-core` |

## 의존 관계

- `ip-guard-core` -> `ip-guard-spi`

## 공개 API 원칙

- `ip-guard-spi`는 핵심 엔진이 따르는 SPI 인터페이스만 유지합니다.
- `ip-guard-core`는 외부 프레임워크에 의존하지 않습니다.
- 입력 모델과 판정 규칙은 `core` 안에서만 확장합니다.

## 상위 모듈과의 관계

`ip-guard`는 1계층 엔진이므로, 상위 플랫폼 모듈이 존재하는 환경에서는 최종 애플리케이션이 `ip-guard-core`를 직접 조립하지 않는 구성을 권장합니다.

권장 경계:

- `ip-guard-core`: `RuleSource`와 `IpGuardEngine`으로 원문 규칙을 파싱하고 판정합니다.
- 상위 플랫폼 모듈: `List<String>` 규칙과 `defaultAllow`를 받아 내부에서 `IpGuardEngine`을 생성하는 facade나 factory를 제공합니다.
- 최종 애플리케이션: 상위 플랫폼 API만 소비하고 `com.ipguard.*` 타입을 직접 import하지 않습니다.

상위 플랫폼이 자체 CIDR matcher로 대체하면 IPv6, range, IPv4 wildcard 같은 `ip-guard` 문법이 줄어들 수 있으므로, 규칙 판정은 가능한 `ip-guard-core`에 위임합니다.
