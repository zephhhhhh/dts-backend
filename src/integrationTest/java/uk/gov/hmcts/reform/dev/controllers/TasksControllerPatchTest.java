package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusBody;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class TasksControllerPatchTest {
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
            MockMvcRequestBuilders.post("/tasks/")
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

        Long createdId = getCreatedTaskId(createResult);

        UpdateTaskStatusBody updateTaskStatusBody = new UpdateTaskStatusBody(TaskStatus.COMPLETED);

        ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.patch("/tasks/" + createdId)
                .contentType("application/json")
                .content(updateTaskStatusBody.toJson())
        );

        result.andExpect(status().isOk())
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

        Long createdId = getCreatedTaskId(createResult);

        ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.patch("/tasks/" + createdId)
                .contentType("application/json")
                .content("{ 'status': 'INVALID' }")
        );

        result.andExpect(status().is4xxClientError());
    }

    @DisplayName("Updating the status of an invalid task fails.")
    @Test
    void updatingStatusToInvalidTaskFails() throws Exception {
        UpdateTaskStatusBody updateTaskStatusBody = new UpdateTaskStatusBody(TaskStatus.COMPLETED);

        ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.patch("/tasks/" + Long.MAX_VALUE)
                .contentType("application/json")
                .content(updateTaskStatusBody.toJson())
        );

        result.andExpect(status().is4xxClientError());
    }
}
