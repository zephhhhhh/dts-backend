package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.reform.dev.SchemaPaths;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TasksControllerDeleteTest")
class TasksControllerDeleteTest extends CommonTasksControllerTest {
    @DisplayName("Deleting a valid task succeeds.")
    @Test
    void deletingValidTaskSucceeds() throws Exception {
        ResultActions createResult = validateCreatedTask(
            createTask(
                "Valid",
                "Valid",
                TaskStatus.TODO,
                TEST_TIME
            ),
            false
        );
        Long id = createdId(createResult.andReturn());

        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/" + id)
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(conformsToSchema(SchemaPaths.TASK_ENTITY))
            .andExpect(jsonPath("id").value(id))
            .andExpect(jsonPath("title").value("Valid"))
            .andExpect(jsonPath("description").value("Valid"))
            .andExpect(jsonPath("status").value(TaskStatus.TODO.toString()))
            .andExpect(jsonPath("due_date").value(TEST_TIME.toString()));
    }

    @DisplayName("Deleting an invalid task fails.")
    @Test
    void deletingInvalidTaskFails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/" + Long.MAX_VALUE)
                .contentType("application/json"))
            .andExpect(status().isNotFound());
    }
}
