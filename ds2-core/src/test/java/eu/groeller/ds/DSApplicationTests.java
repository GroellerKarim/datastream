package eu.groeller.ds;

import eu.groeller.ds.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Runs against the same Postgres the application targets rather than an in-memory stand-in. The
 * context load also runs Flyway and validates the Hibernate mapping against the resulting schema,
 * and both are vendor-specific — checking them on a different database proves nothing about the
 * one in production.
 */
@SpringBootTest
@Import(TestContainersConfig.class)
class DSApplicationTests {

    @Test
    void contextLoads() {
    }

}
