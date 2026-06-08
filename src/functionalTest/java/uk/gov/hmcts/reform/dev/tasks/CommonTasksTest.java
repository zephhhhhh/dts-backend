package uk.gov.hmcts.reform.dev.tasks;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.dev.assertions.CommonResponseAssertions;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.dto.CreateTaskResponse;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
abstract class CommonTasksTest {
    // TODO: I think this should load the port from the .env?
    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    protected static final String TEST_TIME_STRING = "2026-06-05T19:32:30";
    protected static final LocalDateTime TEST_TIME = LocalDateTime.parse(TEST_TIME_STRING);

    protected final CommonResponseAssertions responseAssertions = new CommonResponseAssertions();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    protected Long createTask(String title, String description, TaskStatus status, LocalDateTime dueDate) {
        CreateTaskBody createTaskBody = new CreateTaskBody(title, description, status, dueDate);

        var response = given()
            .contentType(ContentType.JSON)
            .body(createTaskBody.toJson())
            .when()
            .post("/tasks/")
            .then()
            .extract()
            .response();

        responseAssertions.assertStatus(HttpStatus.CREATED, response);

        return response.as(CreateTaskResponse.class).getId();
    }

    protected Response requestTask(Long taskId) {
        return given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/" + taskId.toString())
            .then()
            .extract()
            .response();
    }

    protected Response requestAllTasks() {
        return given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/")
            .then()
            .extract()
            .response();
    }
}
