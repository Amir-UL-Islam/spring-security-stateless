package io.security.base.privilege;

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
import org.springframework.test.context.jdbc.Sql;


@ApplicationModuleTest(
        classes = BaseSecurityApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES
)
public class PrivilegeResourceTest extends BaseIT {

    @Autowired
    public PrivilegeRepository privilegeRepository;

    @Test
    @Sql("/data/privilegeData.sql")
    void getAllPrivileges_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/privileges")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", Matchers.equalTo(2))
                    .body("get(0).id", Matchers.equalTo(1200));
    }

    @Test
    @Sql("/data/privilegeData.sql")
    void getPrivilege_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/privileges/1200")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("name", Matchers.equalTo("Zed diam voluptua."));
    }

    @Test
    void getPrivilege_notFound() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/privileges/1866")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createPrivilege_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/privilegeDTORequest.json"))
                .when()
                    .post("/api/privileges")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, privilegeRepository.count());
    }

    @Test
    void createPrivilege_missingField() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/privilegeDTORequest_missingField.json"))
                .when()
                    .post("/api/privileges")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("name"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/privilegeData.sql")
    void updatePrivilege_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/privilegeDTORequest.json"))
                .when()
                    .put("/api/privileges/1200")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Duis autem vel.", privilegeRepository.findById(((long)1200)).orElseThrow().getName());
        assertEquals(2, privilegeRepository.count());
    }

    @Test
    @Sql("/data/privilegeData.sql")
    void deletePrivilege_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/privileges/1200")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, privilegeRepository.count());
    }

}
