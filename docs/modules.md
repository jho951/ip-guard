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
