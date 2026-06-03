package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SampleFunctionalTest {
    protected static final String CONTENT_TYPE_VALUE = "application/json";

    // TODO: I think this should load the port from the .env?
    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void functionalTest() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get()
            .then()
            .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.asString().startsWith("Welcome"));
    }

    @Test
    void exampleTest() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("tasks/get-example-task")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.asString().startsWith("a"));
    }
}
