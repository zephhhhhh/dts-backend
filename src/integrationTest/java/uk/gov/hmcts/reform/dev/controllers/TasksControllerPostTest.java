package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TasksControllerPostTest")
class TasksControllerPostTest extends CommonTasksControllerTest {
    @DisplayName("A well-formed task creation request should succeed with status 201.")
    @Test
    void typicalTaskCreationSucceeds() throws Exception {
        validateCreatedTask(
            createTask(
                "This is a valid task",
                "There is a description",
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            false
        );
    }

    @DisplayName("A well-formed task creation request with no or empty description specified, "
        + "should succeed with status 201.")
    @Test
    void typicalTaskCreationWithoutDescriptionSucceeds() throws Exception {
        validateCreatedTask(
            createTask(
                "This is a valid task",
                null,
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            false
        );

        validateCreatedTask(
            createTask(
                "This is a valid task too",
                "",
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            false
        );
    }

    @DisplayName("A task with no title should not succeed.")
    @Test
    void taskCreationWithNoTitleFails() throws Exception {
        validateCreatedTask(
            createTask(
                "",
                "The title is empty!",
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            true
        );

        validateCreatedTask(
            createTask(
                null,
                "Title is undefined!",
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            true
        );
    }

    @DisplayName("A task with an invalid status should not succeed.")
    @Test
    void taskCreationWithInvalidStatusFails() throws Exception {
        validateCreatedTask(
            createTaskRaw(
                "Valid title",
                "We have a description!",
                "To-do",
                LocalDateTime.now().toString()
            ),
            true
        );

        validateCreatedTask(
            createTaskRaw(
                "Valid title",
                "We have a description!",
                "done",
                LocalDateTime.now().toString()
            ),
            true
        );
    }

    @DisplayName("A task with an invalid status should not succeed.")
    @Test
    void taskCreationWithInvalidDateFails() throws Exception {
        validateCreatedTask(
            createTaskRaw(
                "Valid title",
                "Here we have a date on the 50th month.. should fail",
                "COMPLETED",
                "2026-50-05T16:55:20.00"
            ),
            true
        );

        validateCreatedTask(
            createTaskRaw(
                "Valid title",
                "We have a description!",
                "COMPLETED",
                "malformed_date" + LocalDateTime.now().toString()
            ),
            true
        );

        validateCreatedTask(
            createTaskRaw(
                "Valid title",
                "Invalid date",
                "COMPLETED",
                "random string"
            ),
            true
        );

        validateCreatedTask(
            createTaskRaw(
                "Valid title",
                "Invalid date format",
                "COMPLETED",
                "2026:06:05T16:55:20.00"
            ),
            true
        );
    }

    @DisplayName("A task with a differing but valid date format should succeed.")
    @Test
    void taskCreationWithValidDateFormatsSucceed() throws Exception {
        validateCreatedTask(
            createTaskRaw(
                "Valid title",
                "Here we have a date with no seconds specified, should succeed",
                "COMPLETED",
                "2026-06-05T18:00"
            ),
            false
        );
    }

    @DisplayName("Task creation with an empty body should fail.")
    @Test
    void taskCreationWithEmptyBodyFails() throws Exception {
        mockMvc.perform(post("/tasks/"))
            .andExpect(status().is4xxClientError());
    }

    @DisplayName("Task creation with an incorrect content-type should fail.")
    @Test
    void taskCreationWithIncorrectContentTypeFails() throws Exception {
        mockMvc.perform(post("/tasks/")
                .contentType("text/html; charset=utf-8")
                .content("random text"))
            .andExpect(status().is4xxClientError());
    }
}
