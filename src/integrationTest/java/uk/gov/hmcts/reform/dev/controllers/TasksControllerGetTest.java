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
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class TasksControllerGetTest {
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

    @DisplayName("Fetching all tasks while there are no tasks should return an empty list.")
    @Test
    void getTasksWhileEmptySucceeds() throws Exception {
        ResultActions result = mockMvc.perform(
            get("/tasks/all")
        );

        result.andExpect(status().isOk());
    }

    @DisplayName("Fetching a task that does exists works.")
    @Test
    void getTaskByIdSucceeds() throws Exception {
        LocalDateTime dueDate = LocalDateTime.now().withNano(0);

        ResultActions createResult = validateCreatedTask(
            createTask(
                "Valid",
                "Valid",
                TaskStatus.TODO,
                dueDate
            ),
            false
        );

        Long createdId = getCreatedTaskId(createResult);

        ResultActions result = mockMvc.perform(
            get("/tasks/" + createdId)
                .contentType("application/json")
        );

        result.andExpect(status().isOk())
            .andExpect(jsonPath("id").value(createdId))
            .andExpect(jsonPath("title").value("Valid"))
            .andExpect(jsonPath("description").value("Valid"))
            .andExpect(jsonPath("status").value(TaskStatus.TODO.toString()))
            .andExpect(jsonPath("dueDate").value(dueDate.toString()));
    }

    @DisplayName("Fetching a task that does not exist fails.")
    @Test
    void getInvalidTaskFails() throws Exception {
        ResultActions result = mockMvc.perform(
            get("/tasks/" + Long.MAX_VALUE)
                .contentType("application/json")
        );

        result.andExpect(status().isNotFound());
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

        Long createdId = getCreatedTaskId(createResult);

        ResultActions result = mockMvc.perform(
            get("/tasks/all")
        );

        result.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].id", hasItem(createdId.intValue())));
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

        Long createdId = getCreatedTaskId(createResult);

        ResultActions result = mockMvc.perform(
            get("/tasks/all")
        );

        result.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].id", hasItem(createdId.intValue())));

        ResultActions deletedResult = mockMvc.perform(
            delete("/tasks/" + createdId)
        );

        deletedResult.andExpect(status().isOk());

        ResultActions afterDeleteResult = mockMvc.perform(
            get("/tasks/all")
        );

        afterDeleteResult.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].id", not(hasItem(createdId.intValue()))));
    }
}
