package uk.gov.hmcts.reform.dev.controllers;

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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class TasksControllerTest {
    @Autowired
    private transient MockMvc mockMvc;

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

        ResultActions result = mockMvc.perform(
            post("/tasks/")
                .contentType("application/json")
                .content(createTaskBody.toJson())
        );

        return result.andExpect(status().isCreated());
    }

    @DisplayName("A well-formed task creation request should succeed with status 201, and the returned result should")
    @Test
    void typicalTaskCreationSucceeds() throws Exception {
        // TODO: Validate a valid ID is returned. Maybe return Id as json?

        createTask(
            "This is a valid task",
            "There is a description",
            TaskStatus.TODO,
            LocalDateTime.now()
        );
    }
}
