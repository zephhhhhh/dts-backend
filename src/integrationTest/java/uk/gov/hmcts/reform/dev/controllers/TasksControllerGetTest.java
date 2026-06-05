package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.ResultActions;

import uk.gov.hmcts.reform.dev.SchemaPaths;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TasksControllerGetTest")
class TasksControllerGetTest extends CommonTasksControllerTest {
    @DisplayName("Fetching all tasks while there are no tasks should return an empty list.")
    @Test
    void getTasksWhileEmptySucceeds() throws Exception {
        getAllTasks();
    }

    @DisplayName("Fetching a task that does exists works.")
    @Test
    void getTaskByIdSucceeds() throws Exception {
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

        mockMvc.perform(get("/tasks/" + id).contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(conformsToSchema(SchemaPaths.TASK_ENTITY))
            .andExpect(jsonPath("id").value(id))
            .andExpect(jsonPath("title").value("Valid"))
            .andExpect(jsonPath("description").value("Valid"))
            .andExpect(jsonPath("status").value(TaskStatus.TODO.toString()))
            .andExpect(jsonPath("due_date").value(TEST_TIME.toString()));
    }

    @DisplayName("Fetching a task that does not exist fails.")
    @Test
    void getInvalidTaskFails() throws Exception {
        mockMvc.perform(get("/tasks/" + Long.MAX_VALUE)
                .contentType("application/json"))
            .andExpect(status().isNotFound());
    }

    @DisplayName("Fetching all tasks after adding a task contains the added task.")
    @Test
    void getAllTasksContainsAddedTask() throws Exception {
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

        getAllTasks().andExpect(jsonPath("$[*].id", hasItem(id.intValue())));
    }

    @DisplayName("Fetching all tasks after deleting a task removes the added task.")
    @Test
    void getAllTasksDoesNotContainDeletedTask() throws Exception {
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

        getAllTasks().andExpect(jsonPath("$[*].id", hasItem(id.intValue())));

        mockMvc.perform(delete("/tasks/" + id))
            .andExpect(status().isOk())
            .andExpect(conformsToSchema(SchemaPaths.TASK_ENTITY));

        getAllTasks().andExpect(jsonPath("$[*].id", not(hasItem(id.intValue()))));
    }
}
