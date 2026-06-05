package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class TasksControllerPostTest {
    @Autowired
    private transient MockMvc mockMvc;

    private Long getCreatedTaskId(ResultActions result) throws UnsupportedEncodingException {
        return Long.parseLong(result.andReturn().getResponse().getContentAsString());
    }

    private void validateValidTaskId(ResultActions result) {
        Assertions.assertDoesNotThrow(() -> {
            getCreatedTaskId(result);
        });
    }

    private ResultActions createTaskRaw(
        String title,
        String description,
        String status,
        String dueDate
    ) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("status", status);
        body.put("due_date", dueDate);
        if (description != null) {
            body.put("description", description);
        }

        return mockMvc.perform(
            post("/tasks/")
                .contentType("application/json")
                .content(Json.pretty(body))
        );
    }

    private ResultActions createTask(
        String title,
        String description,
        TaskStatus status,
        LocalDateTime dueDate
    ) throws Exception {
        CreateTaskBody createTaskBody = new CreateTaskBody();
        createTaskBody.setTitle(title);
        createTaskBody.setDescription(description);
        createTaskBody.setStatus(status);
        createTaskBody.setDueDate(dueDate);

        return mockMvc.perform(
            post("/tasks/")
                .contentType("application/json")
                .content(createTaskBody.toJson())
        );
    }

    private ResultActions validateCreatedTask(
        ResultActions result,
        boolean shouldFail
    ) throws Exception {
        if (shouldFail) {
            result.andExpect(status().is4xxClientError());
        } else {
            result.andExpect(status().isCreated());
            validateValidTaskId(result);
        }

        return result;
    }

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
}
