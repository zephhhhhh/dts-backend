package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Map;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CrudFunctionalTest {
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

    private Long createTask(String title, String description, String status, String dueDate) {
        Map<String, Object> body = Map.of(
            "title", title,
            "description", description,
            "status", status,
            "due_date", dueDate
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("tasks/")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
        return response.as(Long.class);
    }

    private Long createTask(String title, String status, String dueDate) {
        Map<String, Object> body = Map.of(
            "title", title,
            "status", status,
            "due_date", dueDate
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("tasks/")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
        return response.as(Long.class);
    }

    @Test
    void listingAllTasksWhenEmptyReturnsAnEmptyList() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("tasks/all")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void createTaskReturnsGeneratedId() {
        Long createdId = createTask(
            "New task",
            "A task description",
            "To do",
            LocalDateTime.now().toString()
        );

        Assertions.assertNotNull(createdId);
    }

    @Test
    void getTaskByIdReturnsCreatedTask() {
        Long createdId = createTask(
            "Read a task",
            "Fetch it back by id",
            "To do",
            LocalDateTime.now().toString()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("tasks/" + createdId)
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(createdId.intValue(), response.jsonPath().getInt("id"));
        Assertions.assertEquals("Read a task", response.jsonPath().getString("title"));
        Assertions.assertEquals("Fetch it back by id", response.jsonPath().getString("description"));
        Assertions.assertEquals("To do", response.jsonPath().getString("status"));
    }

    @Test
    void getTaskByIdForUnknownTaskDoesNotReturnOk() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("tasks/99999999")
            .then()
            .extract()
            .response();

        Assertions.assertNotEquals(200, response.statusCode());
    }

    @Test
    void getAllTasksIncludesCreatedTask() {
        Long createdId = createTask(
            "Appear in the list",
            "Should be returned by /all",
            "To do",
            LocalDateTime.now().toString()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("tasks/all")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(
            response.jsonPath().getList("id").contains(createdId.intValue()),
            "Expected /tasks/all to contain the newly created task id"
        );
    }

    @Test
    void createTaskWithNoDescription() {
        Long createdId = createTask(
            "No description",
            "To do",
            LocalDateTime.now().toString()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("tasks/" + createdId)
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(createdId.intValue(), response.jsonPath().getInt("id"));
        Assertions.assertEquals("No description", response.jsonPath().getString("title"));
        Assertions.assertEquals("", response.jsonPath().getString("description"));
        Assertions.assertEquals("To do", response.jsonPath().getString("status"));
    }

    @Test
    void updateTaskStatusChangesStatus() {
        Long createdId = createTask(
            "Update my status",
            "Status should change",
            "To do",
            LocalDateTime.now().toString()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "Completed"))
            .when()
            .patch("tasks/" + createdId)
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(createdId.intValue(), response.jsonPath().getInt("id"));
        Assertions.assertEquals("Completed", response.jsonPath().getString("status"));
    }

    @Test
    void deleteTaskRemovesTask() {
        Long createdId = createTask(
            "Delete me",
            "Should be removed",
            "To do",
            LocalDateTime.now().toString()
        );

        Response deleteResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .delete("tasks/" + createdId)
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, deleteResponse.statusCode());
        Assertions.assertEquals(createdId.intValue(), deleteResponse.jsonPath().getInt("id"));

        Response getResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .get("tasks/" + createdId)
            .then()
            .extract()
            .response();

        Assertions.assertNotEquals(200, getResponse.statusCode());
    }
}
