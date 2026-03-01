package io.security.base.role;

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
public class RoleResourceTest extends BaseIT {

    @Autowired
    public RoleRepository roleRepository;

    @Test
    void getAllRoles_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/roles")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1100));
    }

    @Test
    void getAllRoles_filtered() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/roles?filter=1101")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1101));
    }

    @Test
    void getRole_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/roles/1100")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("description", Matchers.equalTo("Xonsectetuer adipiscing."));
    }

    @Test
    void getRole_notFound() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/roles/1766")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createRole_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/roleDTORequest.json"))
                .when()
                    .post("/api/roles")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(3, roleRepository.count());
    }

    @Test
    void createRole_missingField() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/roleDTORequest_missingField.json"))
                .when()
                    .post("/api/roles")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("name"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    void updateRole_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/roleDTORequest.json"))
                .when()
                    .put("/api/roles/1100")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Zed diam voluptua.", roleRepository.findById(((long)1100)).orElseThrow().getDescription());
        assertEquals(2, roleRepository.count());
    }

}
