package uk.gov.hmcts.reform.dev.tasks;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskValidationTest extends CommonTasksTest {
    @Test
    void getTaskByIdFromInvalidTaskIdReturnsNotFound() {
        Response response = requestTask(99999999L);

        responseAssertions.assertStatus(HttpStatus.NOT_FOUND, response);
        responseAssertions.assertResponseContains(response, Map.of(
            "detail", "The requested entity could not be found",
            "title", "Entity Not Found"
        ));
    }

    @Test
    void createTaskWithMissingFieldReturnsBadRequest() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body("{ \"status\": \"TODO\", \"due_date\": \"2026-06-05T19:32:30\" }")
            .when()
            .post("/tasks/")
            .then()
            .extract()
            .response();

        responseAssertions.assertStatus(HttpStatus.BAD_REQUEST, response);
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON.toString(), response.getContentType());
        responseAssertions.assertResponseContains(response, Map.of(
            "detail", "The request does not conform to the required JSON schema",
            "title", "Bad Request"
        ));
    }

    @Test
    void createTaskWithIncorrectContentTypeReturnsContentTypeNotSupported() {
        Response response = given()
            .body("Random body")
            .when()
            .post("/tasks/")
            .then()
            .extract()
            .response();

        responseAssertions.assertStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response);
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON.toString(), response.getContentType());
        responseAssertions.assertResponseContains(response, Map.of(
            "detail", "The Content-Type is not supported. Please use application/json",
            "title", "Unsupported Media Type"
        ));
    }

    @Test
    void createTaskWithMissingFieldsReturnsBadRequestDoesNotConformToSchema() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body("{ \"bad_field\": \"Hello\" }")
            .when()
            .post("/tasks/")
            .then()
            .extract()
            .response();

        responseAssertions.assertStatus(HttpStatus.BAD_REQUEST, response);
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON.toString(), response.getContentType());
        responseAssertions.assertResponseContains(response, Map.of(
            "detail", "The request does not conform to the required JSON schema",
            "title", "Bad Request"
        ));
    }

    @Test
    void createTaskWithMissingBodyReturnsBadRequestBodyCouldNotBeRead() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .post("/tasks/")
            .then()
            .extract()
            .response();

        responseAssertions.assertStatus(HttpStatus.BAD_REQUEST, response);
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON.toString(), response.getContentType());
        responseAssertions.assertResponseContains(response, Map.of(
            "detail", "The request body could not be read. It may be missing or invalid JSON.",
            "title", "Bad Request"
        ));
    }

    @Test
    void updateTaskStatusWithInvalidStatusReturnsBadRequestDoesNotConformToSchema() {
        Long createdId = createTask(
            "Update my status",
            "Status should change",
            TaskStatus.COMPLETED,
            LocalDateTime.now()
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "BAD STATUS"))
            .when()
            .patch("/tasks/" + createdId)
            .then()
            .extract()
            .response();

        responseAssertions.assertStatus(HttpStatus.BAD_REQUEST, response);
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON.toString(), response.getContentType());
        responseAssertions.assertResponseContains(response, Map.of(
            "detail", "The request does not conform to the required JSON schema",
            "title", "Bad Request"
        ));
    }
}
