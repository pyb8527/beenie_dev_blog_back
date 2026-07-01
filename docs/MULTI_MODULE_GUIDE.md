# 멀티모듈 가이드

## 1. 모듈 목록

| 모듈 경로 | 타입 | 설명 |
|---|---|---|
| `core` | 실행 모듈 (bootJar) | web/application/domain/infrastructure 패키지를 포함하는 Spring Boot 애플리케이션 |
| `storage:jpa` | 라이브러리 (jar) | JPA 기반 Repository 구현체 |
| `storage:mybatis` | 라이브러리 (jar) | MyBatis Mapper 구현체 |
| `storage:redis` | 라이브러리 (jar) | Redis 어댑터 구현체 |
| `storage:rabbitmq` | 라이브러리 (jar) | RabbitMQ Producer/Consumer 구현체 |
| `support:common` | 라이브러리 (jar) | 공통 상수, 공통 응답 포맷 |
| `support:exception` | 라이브러리 (jar) | 공통 예외, 에러 코드 |
| `support:logging` | 라이브러리 (jar) | 로깅 설정/유틸(AOP 등) |
| `support:util` | 라이브러리 (jar) | 범용 유틸리티 함수 |

## 2. settings.gradle

```groovy
rootProject.name = 'backend-init'

include 'core'

include 'storage:jpa'
include 'storage:mybatis'
include 'storage:redis'
include 'storage:rabbitmq'

include 'support:common'
include 'support:exception'
include 'support:logging'
include 'support:util'
```

## 3. 모듈 간 의존 관계

```
core
 ├─→ storage:jpa
 ├─→ storage:mybatis
 ├─→ storage:redis
 ├─→ storage:rabbitmq
 ├─→ support:common
 ├─→ support:exception
 ├─→ support:logging
 └─→ support:util

storage:jpa       ─→ support:common, support:exception
storage:mybatis   ─→ support:common, support:exception
storage:redis     ─→ support:common, support:exception
storage:rabbitmq  ─→ support:common, support:exception

support:exception ─→ support:common
support:common/logging/util ─→ (다른 모듈에 의존하지 않음)
```

**규칙**

1. `support:*`는 다른 `support:*` 외에는 어떤 모듈에도 의존하지 않는다. (최하위 라이브러리)
2. `storage:*`는 `support:*`에만 의존할 수 있고, `core`에는 의존할 수 없다.
   - `storage`가 domain의 Port(인터페이스)를 구현해야 하므로 **core → storage 방향이 아니라
     core가 storage 구현체를 `application context`(Bean)로 주입받는 방향**이 되도록
     Port 인터페이스는 core/domain에 두고, storage는 이를 구현만 한다.
   - 즉 `storage`는 컴파일 시점에 `core`의 domain 패키지(Port)를 참조해야 하므로,
     실제로는 `storage → core(domain의 Port만)` 형태의 최소 의존을 갖는다.
     Port를 별도 모듈로 더 분리하고 싶다면 `core-api`(도메인/Port 전용 모듈) 분리를 고려한다. (섹션 6 참고)
3. `core`는 모든 `storage:*`, `support:*` 모듈에 의존할 수 있다.
4. 순환 의존(circular dependency)은 절대 허용하지 않는다.

## 4. build.gradle 컨벤션

### 루트 build.gradle

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.2' apply false
    id 'io.spring.dependency-management' version '1.1.6' apply false
}

ext {
    springBootVersion = '3.3.2'
}

allprojects {
    group = 'com.beenie'
    version = '0.0.1-SNAPSHOT'

    repositories { mavenCentral() }
}

subprojects {
    apply plugin: 'java'
    apply plugin: 'java-library'
    apply plugin: 'io.spring.dependency-management'

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencyManagement {
        imports {
            mavenBom "org.springframework.boot:spring-boot-dependencies:${springBootVersion}"
        }
    }

    dependencies {
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    }

    tasks.named('test') { useJUnitPlatform() }
}
```

`org.springframework.boot` 플러그인은 `core`에만 적용하므로, `storage`/`support` 모듈에는
애초에 `bootJar` 태스크 자체가 생기지 않는다. 대신 `io.spring.dependency-management`로
Spring Boot BOM만 가져와 라이브러리 모듈도 core와 동일한 의존성 버전을 사용하게 한다.

### core/build.gradle (실행 모듈)

```groovy
plugins {
    id 'org.springframework.boot'
}

dependencies {
    implementation project(':storage:jpa')
    implementation project(':storage:mybatis')
    implementation project(':storage:redis')
    implementation project(':storage:rabbitmq')

    implementation project(':support:common')
    implementation project(':support:exception')
    implementation project(':support:logging')
    implementation project(':support:util')

    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'

    testImplementation 'com.tngtech.archunit:archunit-junit5:1.3.0'
}
```

### storage/jpa/build.gradle (라이브러리 모듈 예시)

```groovy
dependencies {
    api project(':support:common')
    api project(':support:exception')

    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.flywaydb:flyway-mysql'
    runtimeOnly 'com.mysql:mysql-connector-j'
}
```

Flyway 마이그레이션 SQL은 `storage/jpa/src/main/resources/db/migration`에 `V{n}__{설명}.sql`
형식으로 추가한다. `core`가 `storage:jpa`에 의존하므로, core 부팅 시 클래스패스에 포함된
이 마이그레이션이 자동으로 적용된다.

## 5. 새 모듈 추가 절차

1. `settings.gradle`에 `include '{path}'` 추가
2. 해당 디렉터리에 `build.gradle` 생성 (필요한 의존성만 최소로 선언)
3. 이 모듈이 의존해도 되는 대상인지 섹션 3의 규칙으로 확인
   - `support`에 추가한다면: 다른 모듈 의존 금지
   - `storage`에 추가한다면: `support`까지만 의존 허용
   - `core`에서 사용한다면: `core/build.gradle`에 `implementation project(':new:module')` 추가
4. 패키지 루트는 `com.beenie.{module}.{sub-package}` 형태로 통일
5. 순환 의존 여부를 `./gradlew :core:dependencies` 등으로 확인

## 6. 향후 확장 시 고려사항

- **core-api(도메인 전용) 모듈 분리**: 현재는 domain(Port 포함)이 `core` 내부 패키지지만,
  `storage:*`가 Port만 참조하도록 더 엄격히 분리하고 싶다면 `core-api`(domain + port)를
  별도 모듈로 빼고 `core`는 `core-api`를 구현/조립하는 실행 모듈로 남길 수 있다.
- **batch/worker 등 추가 실행 모듈**: `storage`, `support`를 재사용하는 두 번째 실행 모듈이
  필요해지면 `core`와 동일한 레벨에 `batch` 모듈을 추가하고 동일한 규칙을 적용한다.
- **의존 방향 강제**: `core/src/test/java/com/beenie/backend/architecture/LayeredArchitectureTest`에서
  ArchUnit `layeredArchitecture()`로 web/application/domain/infrastructure/storage/support 간
  의존 방향을 이미 강제하고 있다. 새 계층/모듈을 추가하면 이 테스트의 `layer(...)` 정의도 함께 갱신한다.
