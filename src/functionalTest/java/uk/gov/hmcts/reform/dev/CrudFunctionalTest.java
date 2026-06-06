package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.dto.CreateTaskResponse;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static io.restassured.RestAssured.given;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO: Document these tests.

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CrudFunctionalTest {
    // TODO: I think this should load the port from the .env?
    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }


    private void assertStatus(HttpStatus status, Response response) {
        assertEquals(status.value(), response.statusCode());
    }

    @Test
    void welcomeReturnsWelcomeMessage() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get()
            .then()
            .extract().response();

        assertStatus(HttpStatus.OK, response);
        assertTrue(response.asString().startsWith("Welcome"));
    }

    private Long createTask(String title, String description, TaskStatus status, LocalDateTime dueDate) {
        CreateTaskBody createTaskBody = new CreateTaskBody(title, description, status, dueDate);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(createTaskBody.toJson())
            .when()
            .post("/tasks/")
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.CREATED, response);

        return response.as(CreateTaskResponse.class).getId();
    }

    @Test
    void listingAllTasksWhenEmptyReturnsAnEmptyList() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("tasks/")
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.OK, response);
    }

    @Test
    void createTaskReturnsGeneratedId() {
        Long createdId = createTask(
            "New task",
            "A task description",
            TaskStatus.TODO,
            LocalDateTime.now()
        );

        assertNotNull(createdId);
    }

    @Test
    void getTaskByIdReturnsCreatedTask() {
        Long createdId = createTask(
            "Read a task",
            "Fetch it back by id",
            TaskStatus.TODO,
            LocalDateTime.now()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/" + createdId)
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.OK, response);
        assertEquals(createdId.intValue(), response.jsonPath().getInt("id"));
        assertEquals("Read a task", response.jsonPath().getString("title"));
        assertEquals("Fetch it back by id", response.jsonPath().getString("description"));
        assertEquals("TODO", response.jsonPath().getString("status"));
    }

    @Test
    void getTaskByIdForUnknownTaskDoesNotReturnOk() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/99999999")
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.NOT_FOUND, response);
    }

    @Test
    void getAllTasksIncludesCreatedTask() {
        Long createdId = createTask(
            "Appear in the list",
            "Should be returned by /all",
            TaskStatus.TODO,
            LocalDateTime.now()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/")
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.OK, response);
        assertTrue(
            response.jsonPath().getList("id").contains(createdId.intValue()),
            "Expected /tasks/all to contain the newly created task id"
        );
    }

    @Test
    void createTaskWithNoDescription() {
        Long createdId = createTask(
            "No description",
            null,
            TaskStatus.STARTED,
            LocalDateTime.now()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/" + createdId)
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.OK, response);
        assertEquals(createdId.intValue(), response.jsonPath().getInt("id"));
        assertEquals("No description", response.jsonPath().getString("title"));
        assertEquals("", response.jsonPath().getString("description"));
        assertEquals("STARTED", response.jsonPath().getString("status"));
    }

    @Test
    void updateTaskStatusChangesStatus() {
        Long createdId = createTask(
            "Update my status",
            "Status should change",
            TaskStatus.COMPLETED,
            LocalDateTime.now()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "COMPLETED"))
            .when()
            .patch("/tasks/" + createdId)
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.OK, response);
        assertEquals(createdId.intValue(), response.jsonPath().getInt("id"));
        assertEquals("COMPLETED", response.jsonPath().getString("status"));
    }

    @Test
    void deleteTaskRemovesTask() {
        Long createdId = createTask(
            "Delete me",
            "Should be removed",
            TaskStatus.TODO,
            LocalDateTime.now()
        );

        Response deleteResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .delete("/tasks/" + createdId)
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.OK, deleteResponse);
        assertEquals(createdId.intValue(), deleteResponse.jsonPath().getInt("id"));

        Response getResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/" + createdId)
            .then()
            .extract()
            .response();

        assertStatus(HttpStatus.NOT_FOUND, getResponse);
    }
}
