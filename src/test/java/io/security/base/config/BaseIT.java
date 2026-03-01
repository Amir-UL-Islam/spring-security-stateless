package io.security.base.config;

import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.util.StreamUtils;
import tools.jackson.databind.ObjectMapper;


/**
 * Abstract base class to be extended by every IT test. Starts the Spring Boot context, with all data
 * wiped out before each test.
 */
@ActiveProfiles("it")
@Sql({"/data/clearAll.sql", "/data/roleData.sql", "/data/usersData.sql"})
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public abstract class BaseIT {

    @LocalServerPort
    public int serverPort;

    @Autowired
    public ObjectMapper objectMapper;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE));
    }

    @SneakyThrows
    public String readResource(final String resourceName) {
        return StreamUtils.copyToString(getClass().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
    }

    public String adminJwtToken() {
        // user admin, expires 2040-01-01
        return "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiJhZG1pbiIsImxvZ2luX3R5cGUiOiJkaXJlY3QiLCJyb2xlcyI6WyJBRE1JTiJdLCJpc3MiOiJib290aWZ5IiwiaWF0IjoxNzY4ODM0MDEwLCJleHAiOjIyMDg5ODg4MDB9." +
                "pVG_xOJfn3JRASQSPbT-eSonrzBJCiBSJuw1gPmzj3VgKZmOLzZ6In9H8OJdqDFhP_-u1XvmBWfL23Z3M5dRxEQrv9cTiHUp_J8lHqedEHpVK-ExSI1ZpYlvrxrNGMG9oAq7vxqTTXUeGVFedSZpKq_Tx24j9L3wx_sgnPErxbtLYexRDcFDjib7k7QTsqOGafBXmh-d4zDnjRhljZmcHihndsHKzUvvgc_-2jD1CfB-MbFx3lVz_CPFVBHleQcFTElhsTkwcI3Y9BFgPOji1K8sOXWg-8wW_ETiLRiy50G32kZg_awemVHlaghm_MI9vqdEvKiucm6vPoA9AEr3iA";
    }

    public String userJwtToken() {
        // user user, expires 2040-01-01
        return "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiJ1c2VyIiwibG9naW5fdHlwZSI6ImRpcmVjdCIsInJvbGVzIjpbIlVTRVIiXSwiaXNzIjoiYm9vdGlmeSIsImlhdCI6MTc2ODgzNDAxMCwiZXhwIjoyMjA4OTg4ODAwfQ." +
                "cqyDv-o6zsgRQeiAnZwv72642ByYraPVCd-UZUbq3WWkg3Gr5Pk5M_swqMeU-Yz6JKlxUlI6FsoYpRikBhdkYj0vP3w5IUpC0Jl7vOM1Aaanc8rrqTWv8x_n7QMnb3lHgHSGda_qaQqUVXgLEDESkm9J3CkQ3REwcf16ni-PjHW1LP8mamNPtuUQvl-NskU0e7u5xkMG4mK7W3WwOLh4EdKmVPxdg9p1j08ZlBmWHpm2CO_FT5eakpZSUx85KsO8uQ9fq5BMEHffWDuOlciWqp_mJXlDY8tK_KNz2LkP7O9o0Ew72OM461fnlNugAqUNq4Bmg1if_mAsLM1_P3XoQw";
    }

}
