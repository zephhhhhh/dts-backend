package uk.gov.hmcts.reform.dev.tasks;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.dev.models.TaskEntity;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskActionsTest extends CommonTasksTest {
    @Test
    @Order(0)
    void listingAllTasksWhenEmptyReturnsEmptyList() {
        Response response = requestAllTasks();

        responseAssertions.assertStatus(HttpStatus.OK, response);

        TaskEntity[] tasks = response.as(TaskEntity[].class);

        assertNotNull(tasks);
        assertEquals(0L, tasks.length);
    }

    @Test
    void createTaskCreatesTaskAndReturnsGeneratedId() {
        Long createdId = createTask(
            "New task",
            "A task description",
            TaskStatus.TODO,
            TEST_TIME
        );

        assertNotNull(createdId);

        Response response = requestTask(createdId);
        responseAssertions.assertStatus(HttpStatus.OK, response);
        responseAssertions.assertResponseContains(response, Map.of(
            "id", createdId.toString(),
            "title", "New task",
            "description", "A task description",
            "status", "TODO",
            "due_date", TEST_TIME_STRING
        ));
    }

    @Test
    void getTaskByIdReturnsCreatedTask() {
        Long createdId = createTask(
            "Read a task",
            "Fetch it back by id",
            TaskStatus.TODO,
            LocalDateTime.now()
        );

        Response response = requestTask(createdId);

        responseAssertions.assertStatus(HttpStatus.OK, response);
        responseAssertions.assertResponseContains(response, Map.of(
            "id", createdId.toString(),
            "title", "Read a task",
            "description", "Fetch it back by id",
            "status", "TODO"
        ));
    }

    @Test
    void getAllTasksIncludesCreatedTask() {
        Long createdId = createTask(
            "Appear in the list",
            "Should be returned when listing the tasks",
            TaskStatus.TODO,
            LocalDateTime.now()
        );

        Response response = requestAllTasks();

        responseAssertions.assertStatus(HttpStatus.OK, response);
        assertTrue(
            response.jsonPath().getList("id").contains(createdId.intValue()),
            "Expected /tasks/all to contain the newly created task id"
        );
    }

    @Test
    void createTaskWithNoDescriptionCreatesTaskWithBlankDescription() {
        Long createdId = createTask(
            "No description",
            null,
            TaskStatus.STARTED,
            LocalDateTime.now()
        );

        Response response = requestTask(createdId);

        responseAssertions.assertStatus(HttpStatus.OK, response);
        responseAssertions.assertResponseContains(response, Map.of(
            "id", createdId.toString(),
            "title", "No description",
            "description", "",
            "status", "STARTED"
        ));
    }

    @Test
    void updateTaskStatusChangesStatusAndReturnsUpdatedTask() {
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

        responseAssertions.assertStatus(HttpStatus.OK, response);
        responseAssertions.assertResponseContains(response, Map.of(
            "id", createdId.toString(),
            "title", "Update my status",
            "description", "Status should change",
            "status", "COMPLETED"
        ));
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

        responseAssertions.assertStatus(HttpStatus.OK, deleteResponse);
        responseAssertions.assertResponseContains(deleteResponse, Map.of(
            "id", createdId.toString(),
            "title", "Delete me",
            "description", "Should be removed",
            "status", "TODO"
        ));
        assertEquals(createdId.intValue(), deleteResponse.jsonPath().getInt("id"));

        Response getResponse = requestTask(createdId);

        responseAssertions.assertStatus(HttpStatus.NOT_FOUND, getResponse);
    }
}
