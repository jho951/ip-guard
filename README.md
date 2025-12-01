# ip-guard v1

> **단일 JVM 내에서 IP 화이트리스트(IPv4)를 적용하기 위한 경량 라이브러리**  
> 목표: v1은 **IPv4 전용 + 정적(static) 규칙 엔진**

---

## 1. 버전 로드맵

이 저장소는 ip-guard 기능을 단계적으로 확장하는 것을 전제로 한다.

- **v1** (현재)
    - IPv4 전용 화이트리스트 엔진
    - 규칙 표현/파싱/판단 로직 정리
    - jar 모듈 분리 (core / file / servlet / spring)
    - 규칙은 애플리케이션 기동 시 한 번 로드된 후 **정적**으로 사용

- **v2**
    - IPv6 + dual-stack 지원
    - IPv4/IPv6 공통 추상화 (`IpAddress`, `IpRange` 등) 설계

- **v3**
    - 규칙 소스 확장
        - 파일 외 DB, Config Server, Redis 등으로부터 규칙을 읽어오는 `RuleSource` 구현들 추가

- **v4**
    - 규칙 리로드/워처 기능
        - 파일 mtime 기반, 주기 기반 리로드
        - `ReloadStrategy` / `refreshIfNeeded()` 정식 도입

이 README는 **v1에 한정된 스펙을 명세**한다.

---

## 2. 모듈 구조 (jar 단위)

v1에서는 기능을 JAR(라이브러리) 단위로 분리하여,  
어떤 MSA 서비스에서도 필요에 따라 의존성을 추가해 사용할 수 있도록 한다.

```text
ip-guard-root/
├─ ip-guard-core/        # 순수 IP 화이트리스트 엔진 (v1의 핵심)
│  └─ util.com.core.IpUtils
│  └─ com.ipguard.rules.*
│  └─ com.ipguard.engine.IpGuardEngine
│  └─ com.ipguard.error.*
│
├─ ip-guard-file/        # (선택) 파일/환경변수 기반 규칙 로더
│  └─ config.com.core.EnvConfig
│  └─ com.ipguard.file.RuleFileLocator
│  └─ com.ipguard.file.RuleFileLoader
│  └─ com.ipguard.source.DefaultRuleSource
│  └─ com.ipguard.source.FileRuleSource
│
├─ ip-guard-servlet/     # (선택) Servlet 환경용 Filter 어댑터
│  └─ com.ipguard.adapter.servlet.IpGuardFilter
│
└─ ip-guard-spring/      # (선택) Spring/Spring Security 어댑터
   └─ com.ipguard.adapter.spring.IpGuardOncePerRequestFilter
