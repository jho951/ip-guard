# ip-guard

`ip-guard`는 Java 17 기반의 IP 접근 제어 OSS 모듈입니다.
핵심 개념, 입력/출력 모델, 인터페이스 계약, 판정 규칙을 한 묶음으로 제공합니다.

[![Build](https://github.com/jho951/ip-guard/actions/workflows/build.yml/badge.svg)](https://github.com/jho951/ip-guard/actions/workflows/build.yml)
[![Publish](https://github.com/jho951/ip-guard/actions/workflows/publish.yml/badge.svg?branch=v1.0.2)](https://github.com/jho951/ip-guard/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jho951/ip-guard-core?label=maven%20central)](https://central.sonatype.com/search?q=io.github.jho951%20ip-guard)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](./LICENSE)
[![Tag](https://img.shields.io/github/v/tag/jho951/ip-guard)](https://github.com/jho951/ip-guard/tags)

## 공개 좌표

- `io.github.jho951:ip-guard-core`
- `io.github.jho951:ip-guard-spi`

## 무엇을 제공하나

- `ip-guard-spi`: SPI 인터페이스
- `ip-guard-core`: IP 파싱, 룰 파싱/매칭, 결정 엔진

## 빠른 시작

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.jho951:ip-guard-core:<version>")
}
```

## 문서

- [docs/README.md](docs/README.md)
