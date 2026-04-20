# Troubleshooting

`ip-guard`의 1계층 내부에서 자주 만나는 문제와 확인 순서입니다.

## 1. 규칙이 적용되지 않는다

- 규칙 원문이 비어 있거나 전부 주석/공백인지 확인합니다.
- `Single`, `CIDR`, `Range`, `IPv4 wildcard` 중 하나로 해석 가능한지 확인합니다.
- 한 줄에 하나의 규칙만 두는지 확인합니다.
- 주석 뒤의 내용은 파싱되지 않으므로 실제 규칙이 잘려 있지 않은지 확인합니다.

## 2. 특정 IP가 계속 차단된다

- `RuleSource`가 기대한 규칙 원문을 반환하는지 확인합니다.
- 입력 IP가 `IpParser` 기준으로 정상 파싱되는지 확인합니다.
- 매칭되지 않을 때는 `NO_MATCH`가 아니라 입력 문제인지도 같이 봅니다.
- 규칙이 실제로는 다른 IP 대역을 가리키는지 확인합니다.

## 3. `INVALID_IP`가 나온다

- 입력 값이 `null` 또는 blank인지 확인합니다.
- 따옴표, 대괄호, 포트, zone 표기 때문에 주소가 의도와 다르게 들어왔는지 확인합니다.
- IPv4와 IPv6를 구분해서 넣고 있는지 확인합니다.
- 테스트에서는 `IpParser.parse(...)`를 직접 호출해 실패 원인을 좁힙니다.

## 4. 규칙 파싱이 실패한다

- `Single` 형식은 단일 IP만 넣어야 합니다.
- `CIDR` 형식은 주소와 prefix가 함께 있어야 합니다.
- `Range` 형식은 시작 IP와 끝 IP가 같은 패밀리여야 합니다.
- `IPv4 wildcard`는 `192.168.*.*`처럼 연속된 `*`만 허용합니다.
- 전체 문법 예시는 [규칙 문법](./rule-syntax.md)을 확인합니다.
- `10.*.1.*` 같은 비연속 wildcard는 거부됩니다.
- `*.*.*.*`처럼 너무 넓은 wildcard는 거부됩니다.

## 5. IPv4 wildcard가 기대와 다르게 동작한다

- wildcard는 IPv4에서만 지원됩니다.
- wildcard는 왼쪽부터 고정된 옥텟 다음에만 `*`가 올 수 있습니다.
- `192.168.*.*`는 허용되지만 `192.*.1.*`는 거부됩니다.
- wildcard는 내부적으로 CIDR로 변환되므로 prefix 길이가 기대와 같은지 확인합니다.

## 6. CIDR 또는 Range가 기대와 다르게 동작한다

- CIDR prefix가 IP 패밀리와 맞는지 확인합니다.
- Range의 시작 IP와 끝 IP가 같은 패밀리인지 확인합니다.
- Range의 시작값이 끝값보다 크지 않은지 확인합니다.
- 입력 IP와 규칙 IP가 같은 IPv4/IPv6 계열인지 확인합니다.

## 7. IPv6 입력이 이상하게 보인다

- `[IPv6]:port` 형식이면 대괄호 안의 주소만 파싱됩니다.
- `fe80::1%en0` 같은 zone 표기는 zone 뒤가 잘립니다.
- `::ffff:127.0.0.1` 같은 IPv6 표현이 기대한 패밀리로 들어오는지 확인합니다.
- 테스트에서는 원문 문자열과 정규화 후 문자열을 분리해서 확인합니다.

## 8. 빈 규칙인데도 결과가 다르다

- 규칙이 없으면 `defaultAllow` 값에 따라 `DEFAULT_ALLOW` 또는 `DEFAULT_DENY`가 나옵니다.
- `RuleSource`가 공백 문자열을 반환하면 빈 규칙으로 처리됩니다.
- 주석만 있는 규칙 파일도 빈 규칙으로 처리됩니다.

## 9. 판정 결과는 맞는데 reason이 다르다

- 허용 시 `MATCHED_RULE` 또는 `DEFAULT_ALLOW`가 나오는지 확인합니다.
- 거부 시 `NO_MATCH`, `DEFAULT_DENY`, `INVALID_IP` 중 무엇인지 구분합니다.
- `Decision`의 reason은 결과 설명용이므로 구현 내부 예외 메시지와 같지 않을 수 있습니다.

## 10. 먼저 확인할 파일

- [`ip-guard-core/src/main/java/com/ipguard/core/engine/IpGuardEngine.java`](/Users/jhons/Downloads/BE/module/ip-guard/ip-guard-core/src/main/java/com/ipguard/core/engine/IpGuardEngine.java)
- [`ip-guard-core/src/main/java/com/ipguard/core/rules/RuleParser.java`](/Users/jhons/Downloads/BE/module/ip-guard/ip-guard-core/src/main/java/com/ipguard/core/rules/RuleParser.java)
- [`ip-guard-core/src/main/java/com/ipguard/core/ip/IpParser.java`](/Users/jhons/Downloads/BE/module/ip-guard/ip-guard-core/src/main/java/com/ipguard/core/ip/IpParser.java)
- [`ip-guard-core/src/main/java/com/ipguard/core/rules/RuleSet.java`](/Users/jhons/Downloads/BE/module/ip-guard/ip-guard-core/src/main/java/com/ipguard/core/rules/RuleSet.java)
- [`ip-guard-core/src/main/java/com/ipguard/core/decision/Decision.java`](/Users/jhons/Downloads/BE/module/ip-guard/ip-guard-core/src/main/java/com/ipguard/core/decision/Decision.java)
