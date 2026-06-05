package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.reform.dev.SchemaPaths;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusBody;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TasksControllerPatchTest")
class TasksControllerPatchTest extends CommonTasksControllerTest {
    @DisplayName("Updating the status of a valid task succeeds.")
    @Test
    void updatingStatusWorks() throws Exception {
        ResultActions createResult = validateCreatedTask(
            createTask(
                "Valid",
                "Valid",
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            false
        );
        Long createdId = createdId(createResult.andReturn());

        UpdateTaskStatusBody updateTaskStatusBody = new UpdateTaskStatusBody(TaskStatus.COMPLETED);
        mockMvc.perform(
            MockMvcRequestBuilders.patch(
                "/tasks/" + createdId)
                .contentType("application/json")
                .content(updateTaskStatusBody.toJson())
            ).andExpect(status().isOk())
            .andExpect(conformsToSchema(SchemaPaths.TASK_ENTITY))
            .andExpect(jsonPath("status").value("COMPLETED"));
    }

    @DisplayName("Updating the status of a valid task to an invalid status fails.")
    @Test
    void updatingStatusToInvalidStatusFails() throws Exception {
        ResultActions createResult = validateCreatedTask(
            createTask(
                "Valid",
                "Valid",
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            false
        );
        Long id = createdId(createResult.andReturn());

        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/" + id)
                .contentType("application/json")
                .content("{ \"status\": \"INVALID\" }"))
            .andExpect(status().is4xxClientError());
    }

    @DisplayName("Updating the status of an invalid task fails.")
    @Test
    void updatingStatusToInvalidTaskFails() throws Exception {
        UpdateTaskStatusBody updateTaskStatusBody = new UpdateTaskStatusBody(TaskStatus.COMPLETED);

        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/" + Long.MAX_VALUE)
                .contentType("application/json")
                .content(updateTaskStatusBody.toJson()))
            .andExpect(status().isNotFound());
    }

    @DisplayName("Updating the status of a task with an empty body fails.")
    @Test
    void updatingStatusWithEmptyBodyFails() throws Exception {
        ResultActions createResult = validateCreatedTask(
            createTask(
                "Valid",
                "Valid",
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            false
        );
        Long id = createdId(createResult.andReturn());

        mockMvc.perform(post("/tasks/" + id))
            .andExpect(status().is4xxClientError());
    }

    @DisplayName("Updating the status of a task with an incorrect content-type fails.")
    @Test
    void updatingStatusWithWrongContentTypeFails() throws Exception {
        ResultActions createResult = validateCreatedTask(
            createTask(
                "Valid",
                "Valid",
                TaskStatus.TODO,
                LocalDateTime.now()
            ),
            false
        );
        Long id = createdId(createResult.andReturn());

        mockMvc.perform(post("/tasks/" + id)
                .contentType("text/html; charset=utf-8")
                .content("random text"))
            .andExpect(status().is4xxClientError());
    }
}
