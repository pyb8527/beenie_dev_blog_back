# 아키텍처 구성도

## 1. 개요

이 프로젝트는 **단일 실행 모듈(core) + 계층형 패키지 구조**를 기본으로 하고,
영속성/메시징처럼 기술 교체 가능성이 있는 부분만 **별도 모듈(storage)**로 분리한
경량 헥사고날(Hexagonal / Ports & Adapters) 구조를 따릅니다.

```
                        ┌─────────────────────────────┐
                        │              core             │
                        │   (실행 가능한 Spring Boot 모듈)  │
                        │                               │
                        │   ┌───────────────────────┐   │
                        │   │         web           │   │
                        │   │  Controller / DTO(요청) │   │
                        │   └───────────┬───────────┘   │
                        │               │ 호출            │
                        │   ┌───────────▼───────────┐   │
                        │   │      application       │   │
                        │   │  UseCase / Service      │   │
                        │   └───────────┬───────────┘   │
                        │               │ 호출/의존        │
                        │   ┌───────────▼───────────┐   │
                        │   │        domain          │   │
                        │   │ Entity / VO / Port(iface)│  │
                        │   └───────────▲───────────┘   │
                        │               │ 구현            │
                        │   ┌───────────┴───────────┐   │
                        │   │     infrastructure      │   │
                        │   │ Security/OAuth/S3 Adapter│  │
                        │   └───────────┬───────────┘   │
                        └───────────────┼───────────────┘
                                        │ 구현/의존
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
              ┌─────▼─────┐      ┌──────▼──────┐     ┌──────▼──────┐
              │ storage:jpa│      │storage:redis│     │storage:rabbitmq│
              └───────────┘      └─────────────┘     └───────────────┘

                 support:common / exception / logging / util
                 (모든 계층/모듈에서 공통으로 참조 가능한 최하위 모듈)
```

## 2. 계층 책임

### core/web
- REST Controller, 요청/응답 DTO, 전역 예외 핸들러(`GlobalExceptionHandler`, `@RestControllerAdvice`)
- 외부 요청을 받아 `application` 계층의 UseCase를 호출하는 역할만 수행
- 비즈니스 로직을 포함하지 않는다
- 모든 응답은 `support:common`의 `ApiResponse<T>`로 감싼다. 자세한 컨벤션은
  [API_CONVENTION.md](API_CONVENTION.md) 참고

### core/application
- UseCase(또는 Service) 구현체와 트랜잭션 경계(`@Transactional`)
- 여러 도메인 객체/Port를 조합해 하나의 유스케이스를 완성
- 외부 기술(Spring Web, JPA 등)에 대한 직접 의존을 지양

### core/domain
- Entity, Value Object, 도메인 서비스, 그리고 **Port(인터페이스)** 정의
- Port 예시: `UserRepository`, `TokenStorage`, `EventPublisher` 등
- 프레임워크/인프라에 대한 의존이 전혀 없어야 하는 순수 계층

### core/infrastructure
- GitHub OAuth 클라이언트, S3 업로더, Security 설정(`SecurityConfig`, `JwtFilter`) 등
- domain의 Port를 구현하거나, 외부 API/기술과 직접 통신하는 어댑터
- `storage` 모듈의 구현체를 조립/설정하는 위치이기도 함

### storage/*
- domain에서 정의한 Port(예: Repository 인터페이스)의 **기술별 구현체**
- `jpa`, `mybatis`, `redis`, `rabbitmq`처럼 기술 단위로 모듈을 분리해
  특정 기술을 교체하거나 제거할 때 다른 모듈에 영향을 주지 않도록 함

### support/*
- 어떤 계층에서도 참조할 수 있는 순수 유틸리티/공통 예외/로깅 설정
- `support:common`: 공통 응답 포맷(`ApiResponse<T>`) 등
- `support:exception`: 공통 예외(`BusinessException`)와 에러 코드(`ErrorCode`)
- 비즈니스 로직이나 프레임워크 종속 코드를 포함하지 않는다

## 3. 의존 방향 원칙

```
web → application → domain ← infrastructure
                        ↑
                     storage:*
```

- **domain은 아무것도 의존하지 않는다.** (core 내부에서도 최하위)
- `infrastructure`, `storage:*`는 domain의 Port를 **구현**하는 방향으로만 의존한다.
- `web`은 `application`만 알고, `application`은 `domain`만 안다.
- `support:*`는 모든 모듈에서 참조 가능한 유일한 예외 모듈이다.
- 역방향 의존(예: domain → infrastructure)은 금지한다.

## 4. 요청 처리 흐름 예시

```
Client
  → core/web (Controller)
  → core/application (UseCase)
  → core/domain (Entity/VO 로직 수행, Port 호출)
  → storage:jpa (Port 구현체가 실제 DB 접근)
  → 응답 반환 (역순으로 전파)
```

## 5. 왜 core를 단일 모듈로 두었나

- `web/application/domain/infrastructure`는 항상 함께 배포되는 실행 단위이므로,
  모듈로 쪼개면 얻는 이득(독립 배포, 독립 재사용) 없이 빌드 복잡도만 늘어난다.
- 반면 `storage`, `support`는 여러 실행 모듈(향후 batch, worker 등)에서
  재사용될 가능성이 있어 별도 모듈로 분리했다.
- 계층 간 경계는 모듈이 아닌 **패키지 + 코드 컨벤션(ArchUnit 등)**으로 강제한다.
  자세한 내용은 [MULTI_MODULE_GUIDE.md](MULTI_MODULE_GUIDE.md)를 참고.
