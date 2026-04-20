# ip-guard

`ip-guard`는 Java 17 기반의 IP 접근 제어 OSS 모듈입니다.
핵심 개념, 입력/출력 모델, 인터페이스 계약, 판정 규칙을 한 묶음으로 제공합니다.

[![Build](https://github.com/jho951/ip-guard/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ip-guard/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/ip-guard-core?label=maven%20central)](https://central.sonatype.com/search?q=jho951)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](./LICENSE)
[![Tag](https://img.shields.io/github/v/tag/jho951/ip-guard)](https://github.com/jho951/ip-guard/tags)

## 공개 좌표

- `io.github.jho951:ip-guard-core`
- `io.github.jho951:ip-guard-spi`

## 무엇을 제공하나

- `ip-guard-spi`: SPI 인터페이스
- `ip-guard-core`: IP 파싱, 룰 파싱/매칭, 결정 엔진
- IPv4/IPv6 single, CIDR, range, IPv4 wildcard 규칙 문법

## 소비 방식

`ip-guard`는 1계층 IP 접근 제어 엔진입니다. 상위 플랫폼이나 서비스 경계에서 `ip-guard`를 감싸는 모듈이 있다면, 애플리케이션은 그 2계층 API를 우선 사용해야 합니다.

예를 들어 플랫폼 보안 모듈이 `ip-guard`를 감싼다면 gateway 같은 소비자는 `com.ipguard.*` 타입을 직접 import하지 않고, 플랫폼 보안 모듈의 facade나 factory를 통해 규칙 목록과 `defaultAllow`만 전달하는 형태를 권장합니다.

직접 `ip-guard-core`를 소비하는 경우에만 `IpGuardEngine`과 `RuleSource`를 생성합니다.

## 빠른 시작

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.jho951:ip-guard-core:<version>")
}
```

## 규칙 문법

규칙은 한 줄에 하나씩 작성합니다. 빈 줄은 무시되고, `#` 또는 `//` 뒤의 내용은 주석으로 처리됩니다.

```text
# Single
127.0.0.1
2001:db8::1

# CIDR
192.168.0.0/16
2001:db8::/32

# Range
192.168.0.1-192.168.0.255
2001:db8::1-2001:db8::f

# IPv4 wildcard
192.168.*.*
10.*.*.*
```

IPv4 wildcard는 뒤쪽에 연속된 `*`만 허용합니다. 예를 들어 `192.168.*.*`는 허용되지만 `10.*.1.*`와 `*.*.*.*`는 거부됩니다.

## 문서

- [docs/README.md](docs/README.md)
- [규칙 문법](docs/rule-syntax.md)
- [아키텍처](docs/architecture.md)
