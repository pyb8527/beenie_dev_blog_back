# backend-init

Spring Boot 기반 멀티모듈 백엔드 프로젝트입니다.

## 프로젝트 구조

```
backend-init
│
├── core                  # 실행 가능한 Spring Boot 애플리케이션 모듈
│   ├── BackendApplication.java
│   ├── web               # Controller, Request/Response, 예외 핸들러
│   ├── application       # UseCase, Service, DTO
│   ├── domain            # Entity, VO, Domain Service, Port(인터페이스)
│   └── infrastructure    # Adapter (GitHub OAuth, S3, Security 등)
│
├── storage
│   ├── jpa               # JPA Repository 구현체
│   ├── mybatis           # MyBatis Mapper
│   ├── redis             # Redis Adapter
│   └── rabbitmq          # MQ Producer/Consumer
│
├── support
│   ├── common
│   ├── exception
│   ├── logging
│   └── util
│
├── docs                  # 아키텍처/가이드 문서
├── docker                # 로컬/배포용 docker 설정
│
├── build.gradle
└── settings.gradle
```

모듈 구조와 의존 방향에 대한 자세한 설명은 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)와
[docs/MULTI_MODULE_GUIDE.md](docs/MULTI_MODULE_GUIDE.md)를 참고하세요.

## 기술 스택

| 영역 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.2 |
| Build | Gradle (Multi-Module) |
| Persistence | JPA(Hibernate), MyBatis |
| Migration | Flyway |
| Cache/Session | Redis |
| Messaging | RabbitMQ |
| API 문서 | springdoc-openapi (Swagger UI) |
| Monitoring | Spring Boot Actuator |
| Architecture Test | ArchUnit |
| Container | Docker / Docker Compose |

> 실제 도입 여부에 맞게 표를 업데이트하세요.

## 시작하기

### 요구 사항

- JDK 21+
- Docker / Docker Compose (로컬 인프라: MySQL, Redis, RabbitMQ 등)

### 로컬 인프라 실행

```bash
docker compose -f docker/docker-compose.yml up -d
```

> 호스트에 이미 MySQL(3306)이 떠 있는 경우와 충돌하지 않도록 MySQL은 `3307:3306`으로 매핑되어 있습니다.

### 빌드

```bash
./gradlew clean build
```

### 실행

```bash
./gradlew :core:bootRun
```

기본 활성 프로필은 `local`이며, `core/src/main/resources/application-local.yml`이 로컬 docker-compose 인프라를 바라보도록 설정되어 있습니다.

### 실행 후 확인

| 항목 | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI 문서 | http://localhost:8080/v3/api-docs |
| Health Check | http://localhost:8080/actuator/health |

첫 부팅 시 `storage/jpa`의 `src/main/resources/db/migration` 아래 Flyway 마이그레이션이 자동 적용됩니다.

## 프로필

| 프로필 | 용도 | 비고 |
|---|---|---|
| `local` | 로컬 개발 (기본값) | `docker/docker-compose.yml` 인프라 사용, `show-sql: true` |
| `dev` | 개발 서버 배포 | DB/Redis/RabbitMQ 접속 정보를 환경변수로 주입 |
| `prod` | 운영 배포 | Swagger UI 비활성화, health 상세 정보 비노출 |

## 보안 기본값

`core/infrastructure/security/SecurityConfig`는 `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health/**`만
인증 없이 허용하고 나머지 요청은 `authenticated()`로 막아둔 상태입니다. 아직 로그인 메커니즘(JWT/GitHub OAuth 등)이
붙지 않았기 때문에 그 외 API는 인증 수단 부재로 403이 반환됩니다. 실제 인증 방식을 `infrastructure`에 구현하면서
허용 경로/인증 로직을 함께 채워나가면 됩니다.

## 공통 API 응답 / 예외 처리

- `support:common`의 `ApiResponse<T>`가 모든 응답을 `{ success, data, error }` 형태로 감쌉니다.
- `support:exception`의 `ErrorCode`(에러코드/HTTP 상태/메시지)와 `BusinessException`으로 도메인/서비스단
  예외를 표현합니다.
- `core/web`의 `GlobalExceptionHandler`가 `BusinessException`, `@Valid` 검증 실패,
  그 외 예상치 못한 예외를 모두 `ApiResponse` 형태의 에러 응답으로 변환합니다.
- 사용법과 응답 예시는 [docs/API_CONVENTION.md](docs/API_CONVENTION.md) 참고.

## 아키텍처 검증

`core/src/test/.../architecture/LayeredArchitectureTest`가 ArchUnit으로 계층 의존 방향
(`web → application → domain ← infrastructure ← storage`)을 테스트 단계에서 강제합니다.
규칙을 위반하는 의존성이 추가되면 `./gradlew :core:test`가 실패합니다.

## 모듈 개요

| 모듈 | 역할 |
|---|---|
| `core` | 실행 모듈. web/application/domain/infrastructure 계층을 패키지로 포함 |
| `storage:jpa` | JPA 기반 영속성 어댑터 구현 |
| `storage:mybatis` | MyBatis 기반 영속성 어댑터 구현 |
| `storage:redis` | Redis 어댑터 구현 |
| `storage:rabbitmq` | RabbitMQ Producer/Consumer 구현 |
| `support:common` | 공통 상수, 응답 포맷 등 |
| `support:exception` | 공통 예외 및 예외 코드 |
| `support:logging` | 로깅 설정/유틸 |
| `support:util` | 범용 유틸리티 |

## 코딩/커밋 컨벤션

- 브랜치: `feature/{issue-no}-{설명}`, `fix/{issue-no}-{설명}`
- 커밋 메시지: `[FEAT] ...`, `[FIX] ...`, `[REFACTOR] ...`, `[DOCS] ...`, `[CHORE] ...`
- 패키지/모듈 추가 규칙은 [docs/MULTI_MODULE_GUIDE.md](docs/MULTI_MODULE_GUIDE.md) 참고

## 문서

- [아키텍처 구성도](docs/ARCHITECTURE.md)
- [멀티모듈 가이드](docs/MULTI_MODULE_GUIDE.md)
- [공통 API 응답 / 예외 처리 컨벤션](docs/API_CONVENTION.md)
