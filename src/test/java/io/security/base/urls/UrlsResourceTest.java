package io.security.base.urls;

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
public class UrlsResourceTest extends BaseIT {

    @Autowired
    public UrlsRepository urlsRepository;

    @Test
    @Sql({"/data/privilegeData.sql", "/data/urlsData.sql"})
    void getAllUrlss_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/urls")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1300));
    }

    @Test
    @Sql({"/data/privilegeData.sql", "/data/urlsData.sql"})
    void getAllUrlss_filtered() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/urls?filter=1301")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1301));
    }

    @Test
    @Sql({"/data/privilegeData.sql", "/data/urlsData.sql"})
    void getUrls_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/urls/1300")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("endpoint", Matchers.equalTo("Consetetur sadipscing."));
    }

    @Test
    void getUrls_notFound() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/urls/1966")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    @Sql("/data/privilegeData.sql")
    void createUrls_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/urlsDTORequest.json"))
                .when()
                    .post("/api/urls")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, urlsRepository.count());
    }

    @Test
    void createUrls_missingField() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/urlsDTORequest_missingField.json"))
                .when()
                    .post("/api/urls")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("endpoint"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql({"/data/privilegeData.sql", "/data/urlsData.sql"})
    void updateUrls_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/urlsDTORequest.json"))
                .when()
                    .put("/api/urls/1300")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Sed ut perspiciatis.", urlsRepository.findById(((long)1300)).orElseThrow().getEndpoint());
        assertEquals(2, urlsRepository.count());
    }

    @Test
    @Sql({"/data/privilegeData.sql", "/data/urlsData.sql"})
    void deleteUrls_success() {
        RestAssured
                .given()
                    .header(HttpHeaders.AUTHORIZATION, adminJwtToken())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/urls/1300")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, urlsRepository.count());
    }

}
