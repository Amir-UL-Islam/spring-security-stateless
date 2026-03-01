package io.security.web;

import io.security.BaseSecurityApplication;
import io.security.base.config.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(
        classes = BaseSecurityApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class StatelessSecurityApplicationTest extends BaseIT {

    @Test
    void contextLoads() {
    }

}
