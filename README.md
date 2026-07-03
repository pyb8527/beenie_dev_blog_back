# beenie_dev_blog_back

**Beenie Dev Blog** 백엔드 — Spring Boot 기반 멀티모듈, 헥사고날(포트-어댑터) 아키텍처의 개인 기술 블로그 API 서버입니다.

게시글 · 카테고리 · 태그 · 댓글 · 검색 · GitHub OAuth 로그인 · 좋아요/북마크 · 관리자 대시보드 · RSS/Sitemap · 이미지 업로드(S3/MinIO)를 제공합니다.

## 프로젝트 구조

```
beenie_dev_blog_back
│
├── core                  # 실행 가능한 Spring Boot 애플리케이션 모듈
│   ├── web               # Controller, Request/Response, GlobalExceptionHandler
│   ├── application       # UseCase / Service, Command·Result DTO
│   ├── domain            # Entity, VO, Domain Service, Port(Repository 인터페이스)
│   └── infrastructure    # Adapter (Security/JWT, GitHub OAuth2, S3, Markdown, Persistence 등)
│
├── storage
│   ├── jpa               # JPA Repository 구현체 + Flyway 마이그레이션(db/migration)
│   ├── mybatis           # 검색/통계 등 동적 쿼리용 MyBatis Mapper
│   ├── redis             # 조회수·방문자 통계·리프레시 토큰 등 Redis 어댑터
│   └── rabbitmq          # 조회수 이벤트 Producer/Consumer
│
├── support
│   ├── common            # 공통 응답 포맷(ApiResponse), 페이지 응답
│   ├── exception         # ErrorCode, BusinessException
│   ├── logging           # 로깅 설정/유틸
│   └── util              # SlugGenerator 등 범용 유틸
│
├── docs                  # 아키텍처/가이드 문서
├── docker                # docker-compose (인프라 + 백엔드 컨테이너)
├── Dockerfile            # core:bootJar 빌드 → 실행 이미지
│
├── build.gradle
└── settings.gradle
```

모듈 의존 방향과 패키지 규칙은 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md), [docs/MULTI_MODULE_GUIDE.md](docs/MULTI_MODULE_GUIDE.md)를 참고하세요.

## 기술 스택

| 영역 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.2 |
| Build | Gradle (Multi-Module) |
| Persistence | Spring Data JPA(Hibernate), MyBatis |
| Migration | Flyway |
| Auth | Spring Security, GitHub OAuth2, JWT(jjwt) |
| Cache / Stats | Redis |
| Messaging | RabbitMQ |
| File Storage | AWS SDK for S3 (MinIO 호환) |
| Markdown | flexmark-java + HTML sanitizer |
| API 문서 | springdoc-openapi (Swagger UI) |
| Monitoring | Spring Boot Actuator |
| Architecture Test | ArchUnit |
| Env 관리 | spring-dotenv (`.env`) |
| Container | Docker / Docker Compose |

## 시작하기

### 요구 사항

- JDK 21+
- Docker / Docker Compose
- GitHub OAuth App (Client ID/Secret) — 로그인 기능 사용 시

### 방법 1. 전체를 Docker로 실행 (권장)

인프라(MySQL·Redis·RabbitMQ·MinIO)와 백엔드 애플리케이션을 한 번에 컨테이너로 띄웁니다. 백엔드는 `dev` 프로필로 뜨며 접속 정보/시크릿은 `docker/docker-compose.yml`의 `backend.environment`에서 주입됩니다.

```bash
docker compose -f docker/docker-compose.yml up -d --build
```

> 코드를 수정했으면 `--build`로 이미지를 다시 빌드하세요. 백엔드만 재빌드하려면 `... up -d --build backend`.

### 방법 2. 인프라만 Docker, 앱은 호스트에서 실행

```bash
# 1) 인프라만 기동
docker compose -f docker/docker-compose.yml up -d mysql redis rabbitmq minio

# 2) 환경변수 준비 (GitHub OAuth, S3 등 값 채우기)
cp .env.example .env

# 3) dev 프로필로 실행 (spring-dotenv가 .env 자동 로드)
./gradlew :core:bootRun --args='--spring.profiles.active=dev'
```

> **주의:** 기본 활성 프로필인 `local`에는 인프라 접속 정보만 있고 **인증/스토리지 설정이 없어** 앱이 완전히 기동되지 않습니다. 전체 기능을 쓰려면 위처럼 `dev` 프로필 + 환경변수로 실행하세요.

### 실행 후 확인

| 항목 | URL |
|---|---|
| Health Check | http://localhost:8080/actuator/health |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI 문서 | http://localhost:8080/v3/api-docs |
| RabbitMQ 관리 콘솔 | http://localhost:15672 (guest/guest) |
| MinIO 콘솔 | http://localhost:9001 (minioadmin/minioadmin) |

첫 부팅 시 `storage/jpa`의 `db/migration` 아래 Flyway 마이그레이션(`V1`~)이 자동 적용됩니다.

## 로컬 인프라 (docker-compose)

| 서비스 | 이미지 | 호스트 포트 | 비고 |
|---|---|---|---|
| MySQL | mysql:8.0 | `3307` → 3306 | 호스트 3306 충돌 방지용 매핑, DB `beenie_dev_blog_back` |
| Redis | redis:7 | `6379` | 조회수·방문자 통계·토큰 |
| RabbitMQ | rabbitmq:3-management | `5672`, `15672` | 조회수 이벤트 큐 |
| MinIO | minio/minio | `9000`(API), `9001`(콘솔) | S3 호환 이미지 스토리지 |
| Backend | (Dockerfile 빌드) | `8080` | `dev` 프로필 |

## 프로필

| 프로필 | 용도 | 설명 |
|---|---|---|
| `local` | 로컬(기본값) | 인프라 접속만 localhost로 하드코딩(`show-sql: true`). 인증/S3 설정은 없음 |
| `dev` | 개발/도커 실행 | DB·Redis·RabbitMQ·JWT·GitHub OAuth·S3를 **환경변수**로 주입 |
| `prod` | 운영 배포 | Swagger 비활성화, health 상세 비노출, 환경변수 주입 |

`dev`/`prod`의 `${...}` 값은 OS 환경변수 또는 프로젝트 루트의 `.env`(spring-dotenv)에서 채워집니다. 컨테이너 실행 시에는 compose의 `environment`가 직접 주입합니다. 필요한 키 목록은 [.env.example](.env.example)을 참고하세요.

## 인증 / 권한

- **로그인**: GitHub OAuth2 (`/oauth2/authorization/github`). 성공 시 JWT(access/refresh)를 발급해 쿠키로 내려줍니다.
- **권한**: 로그인 사용자는 기본 `USER`. `ALLOWED_GITHUB_ID`(GitHub **숫자 id**)와 일치하는 계정만 `ADMIN`으로 부여되며, 이 값은 로그인마다 재평가됩니다.
- **인가**: `SecurityConfig`가 공개 조회 API·Swagger·`/actuator/health`·OAuth 콜백을 허용하고, 관리자 API(`/api/admin/**`)는 `ADMIN` 권한을 요구합니다. JWT 검증은 `JwtAuthenticationFilter`가 담당합니다.

> `ALLOWED_GITHUB_ID`에는 로그인명이 아니라 숫자 id를 넣어야 합니다. `https://api.github.com/users/<로그인명>`의 `id` 값으로 확인할 수 있습니다.

## 파일 저장 (S3 / MinIO)

이미지 업로드는 S3 호환 스토리지에 저장됩니다. 서버가 스토리지에 **접속하는 주소**와 브라우저에 반환할 **공개 URL**을 분리합니다.

| 키 | 예시(도커) | 용도 |
|---|---|---|
| `S3_ENDPOINT` | `http://minio:9000` | 서버 → MinIO 접속(내부 네트워크) |
| `S3_PUBLIC_URL` | `http://localhost:9000` | 브라우저가 접근할 이미지 URL 베이스(미설정 시 `S3_ENDPOINT`) |

> 운영에서는 `S3_PUBLIC_URL`을 실제 이미지 도메인/CDN으로 지정하세요.

## 주요 API 도메인

| 영역 | 설명 |
|---|---|
| `post` | 게시글 CRUD, 목록/상세, 관리자 게시글 관리 |
| `category` / `tag` | 분류·태그 조회 및 관리 |
| `comment` | 댓글 CRUD, 관리자 댓글 관리 |
| `search` | 제목/본문/태그 통합 검색 (MyBatis) |
| `auth` | GitHub OAuth 로그인, 내 정보, 토큰 재발급 |
| `activity` | 좋아요 / 북마크 |
| `admin` | 대시보드 통계, 사이트/SEO 설정 |
| `feed` | RSS / Sitemap |
| `file` | 이미지 업로드(S3/MinIO) |

## 공통 API 응답 / 예외 처리

- `support:common`의 `ApiResponse<T>`가 모든 응답을 `{ success, data, error }`로 감쌉니다.
- `support:exception`의 `ErrorCode` + `BusinessException`으로 도메인/서비스 예외를 표현합니다.
- `core/web`의 `GlobalExceptionHandler`가 `BusinessException`·`@Valid` 검증 실패·예상치 못한 예외를 모두 `ApiResponse` 에러 형태로 변환합니다.
- 규약과 예시는 [docs/API_CONVENTION.md](docs/API_CONVENTION.md) 참고.

## 아키텍처 검증

`core/src/test/.../architecture/LayeredArchitectureTest`가 ArchUnit으로 계층 의존 방향(`web → application → domain ← infrastructure ← storage`)을 강제합니다. 규칙 위반 시 `./gradlew :core:test`가 실패합니다.

## 빌드 / 테스트

```bash
./gradlew clean build       # 전체 빌드 + 테스트
./gradlew :core:test        # 코어 테스트만
./gradlew :core:bootJar     # 실행 가능한 JAR 생성
```

## 모듈 개요

| 모듈 | 역할 |
|---|---|
| `core` | 실행 모듈. web/application/domain/infrastructure 계층 포함 |
| `storage:jpa` | JPA 영속성 어댑터 + Flyway 마이그레이션 |
| `storage:mybatis` | 검색/통계용 MyBatis Mapper |
| `storage:redis` | Redis 어댑터 |
| `storage:rabbitmq` | RabbitMQ Producer/Consumer |
| `support:common` | 공통 응답 포맷 등 |
| `support:exception` | 공통 예외/예외 코드 |
| `support:logging` | 로깅 설정/유틸 |
| `support:util` | 범용 유틸리티 |

## 코딩/커밋 컨벤션

- 브랜치: `feature/{issue-no}-{설명}`, `fix/{issue-no}-{설명}`
- 커밋 메시지: `[FEAT]`, `[FIX]`, `[REFACTOR]`, `[DOCS]`, `[CHORE]` prefix
- 패키지/모듈 추가 규칙: [docs/MULTI_MODULE_GUIDE.md](docs/MULTI_MODULE_GUIDE.md)

## 문서

- [아키텍처 구성도](docs/ARCHITECTURE.md)
- [멀티모듈 가이드](docs/MULTI_MODULE_GUIDE.md)
- [공통 API 응답 / 예외 처리 컨벤션](docs/API_CONVENTION.md)
