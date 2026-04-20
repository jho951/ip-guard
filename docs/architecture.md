# Architecture

`ip-guard`는 1계층 OSS IP 접근 제어 라이브러리입니다.

## 계층

- `ip-guard-spi`
  - 핵심 엔진이 따르는 SPI 인터페이스만 제공합니다.
- `ip-guard-core`
  - IP 파싱, 규칙 파싱, 규칙 매칭, 판정을 담당합니다.

## 경계 원칙

- 핵심 판정 로직은 `ip-guard-core`에만 둡니다.
- `RuleSource`는 raw text만 반환하고 파싱 책임을 가지지 않습니다.
- 응답 포맷은 최소 공통 형식만 제공하고, 서비스 전용 규약은 강제하지 않습니다.
- 상위 플랫폼 모듈이 `ip-guard`를 감싸는 경우, 최종 애플리케이션은 `com.ipguard.*` 타입보다 상위 플랫폼의 facade나 factory를 우선 사용합니다.
- `ip-guard` 고유 문법은 상위 플랫폼 API에서도 손실 없이 전달되어야 합니다.

## 동작 흐름

1. `RuleSource`가 규칙 원문을 제공합니다.
2. `IpGuardEngine`이 규칙 원문을 파싱해 `RuleSet`을 만듭니다.
3. 요청 IP를 정규화하고 파싱합니다.
4. 규칙 매칭 결과에 따라 `Decision`을 반환합니다.

## 규칙 문법

`ip-guard-core`는 다음 규칙 형식을 지원합니다.

- Single: 단일 IPv4/IPv6 주소
- CIDR: IPv4/IPv6 CIDR 대역
- Range: 같은 패밀리의 시작 IP와 끝 IP
- IPv4 wildcard: `192.168.*.*`처럼 뒤쪽 octet을 `*`로 표현하는 문법

상세한 예시와 제약은 [규칙 문법](./rule-syntax.md)을 봅니다.

## 1계층 OSS 기준

- 특정 서비스 도메인에 종속되지 않습니다.
- 인증/인가 구현이 아니라 IP 접근 제어 엔진입니다.

## 상위 플랫폼 통합 기준

상위 플랫폼 모듈이 `ip-guard`를 2계층 API로 노출할 때는 다음 형태를 권장합니다.

- 소비자는 `List<String>` 같은 raw rule 목록과 `defaultAllow`만 전달합니다.
- 상위 플랫폼 내부에서 `RuleSource`와 `IpGuardEngine`을 생성합니다.
- 소비자 애플리케이션은 `com.ipguard.*` 패키지를 직접 import하지 않습니다.
- IPv6, CIDR, range, IPv4 wildcard 같은 `ip-guard` 문법을 별도 축소 구현으로 대체하지 않습니다.

예시:

```java
PlatformIpGuardFacade.fromRules(rules, defaultAllow);
```

이 방식은 서비스가 2계층 API만 소비하면서도 `ip-guard-core`의 전체 규칙 문법을 사용할 수 있게 합니다.
