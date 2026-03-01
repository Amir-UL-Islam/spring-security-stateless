package io.security.base.users;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.security.BaseSecurityApplication;
import io.security.base.config.BaseIT;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ApplicationModuleTest;


@ApplicationModuleTest(
        classes = BaseSecurityApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES
)
public class UsersResourceTest extends BaseIT {

    @Autowired
    public UsersRepository usersRepository;

    @Test
    void getAllUserss_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/userss")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1000));
    }

    @Test
    void getAllUserss_filtered() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/userss?filter=1001")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1001));
    }

    @Test
    void getAllUserss_unauthorized() {
        RestAssured
                .given()
                    .redirects().follow(false)
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/userss")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .body("code", Matchers.equalTo("AUTHORIZATION_DENIED"));
    }

    @Test
    void getUsers_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/userss/1000")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("name", Matchers.equalTo("Zed diam voluptua."));
    }

    @Test
    void getUsers_notFound() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/userss/1666")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createUsers_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/usersDTORequest.json"))
                .when()
                    .post("/api/userss")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(3, usersRepository.count());
    }

    @Test
    void createUsers_missingField() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/usersDTORequest_missingField.json"))
                .when()
                    .post("/api/userss")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("username"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    void updateUsers_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/usersDTORequest.json"))
                .when()
                    .put("/api/userss/1000")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Duis autem vel.", usersRepository.findById(((long)1000)).orElseThrow().getName());
        assertEquals(2, usersRepository.count());
    }

    @Test
    void deleteUsers_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/userss/1000")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, usersRepository.count());
    }

}
