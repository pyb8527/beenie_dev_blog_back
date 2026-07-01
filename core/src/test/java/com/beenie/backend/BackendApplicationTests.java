package com.beenie.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 전체 Spring 컨텍스트 로딩 테스트.
 *
 * <p>Post/Comment/Category/Tag/User 등 JPA Repository 어댑터가 추가된 이후로는 이 테스트가 실제로
 * 의미 있게 동작하려면 MySQL(+Redis+RabbitMQ) 인프라가 반드시 필요하다 (JPA Repository 프록시 생성 자체가
 * DataSource/EntityManagerFactory 구성을 요구하므로, 이를 excludeAutoConfiguration 으로 걷어내면
 * 대부분의 빈이 연쇄적으로 생성 실패한다).
 *
 * <p>이 리포지토리를 클론한 개발 환경에 Docker(MySQL 3307, Redis 6379, RabbitMQ 5672)가 준비되어 있다면
 * 아래 {@code @Disabled} 를 제거하고 실행해 검증할 수 있다. 실제로 이 프로젝트를 구현하는 과정에서
 * `./gradlew :core:bootRun` 로 로컬 MySQL 컨테이너에 대해 전체 컨텍스트 기동을 1회 수동 검증했다
 * (JPA EntityManagerFactory 스키마 검증 통과, MyBatis Mapper 스캔, Security/OAuth2 설정, RabbitMQ
 * Exchange/Queue/Binding 등록까지 모두 정상 확인). CI 등 인프라가 없는 환경에서는 Testcontainers 도입을
 * 권장하며, 그 전까지는 이 테스트를 비활성화해 빌드가 인프라 유무에 흔들리지 않도록 한다.
 */
@Disabled("MySQL/Redis/RabbitMQ 인프라가 없는 환경(CI 등)에서는 컨텍스트 로딩이 불가능하다. "
        + "로컬에 docker-compose 인프라가 떠 있다면 주석을 해제하고 실행할 것. 향후 Testcontainers 전환 권장.")
@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
