package io.security.base.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.security.BaseSecurityApplication;
import io.security.base.config.BaseIT;
import io.security.base.users.UsersRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ApplicationModuleTest;


@ApplicationModuleTest(
        classes = BaseSecurityApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES
)
public class RegistrationResourceTest extends BaseIT {

    @Autowired
    public UsersRepository usersRepository;

    @Test
    void register_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/registrationRequest.json"))
                .when()
                    .post("/register")
                .then()
                    .statusCode(HttpStatus.OK.value());
            assertEquals(3, usersRepository.count());
        }

    }
