package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import uk.gov.hmcts.reform.dev.SchemaPaths;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.service.JsonSchemaValidationService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@DisplayName("CommonTasksControllerTest")
public abstract class CommonTasksControllerTest {
    @Autowired
    protected transient MockMvc mockMvc;

    @MockitoSpyBean
    protected JsonSchemaValidationService jsonSchemaValidationService;

    protected static final ObjectMapper MAPPER = new ObjectMapper();
    protected static final LocalDateTime TEST_TIME = LocalDateTime.parse("2026-06-05T19:32:30");

    protected static Long createdId(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String json = result.getResponse().getContentAsString();
        return MAPPER.readTree(json).get("id").asLong();
    }

    protected ResultActions createTask(
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

    protected ResultActions createTaskRaw(
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

    protected ResultActions getAllTasks() throws Exception {
        return validateAllTasks(mockMvc.perform(get("/tasks/")));
    }

    protected ResultActions validateCreatedTask(
        ResultActions result,
        boolean shouldFail
    ) throws Exception {
        if (shouldFail) {
            result.andExpect(status().is4xxClientError());
        } else {
            result.andExpect(status().isCreated())
                .andExpect(hasValidId())
                .andExpect(conformsToSchema(SchemaPaths.CREATE_TASK_RESPONSE_BODY));
        }

        return result;
    }

    protected ResultActions validateAllTasks(ResultActions result) throws Exception {
        return result.andExpect(status().isOk())
            .andExpect(conformsToSchema(SchemaPaths.ALL_TASKS_RESPONSE))
            .andExpect(jsonPath("$").isArray());
    }

    protected ResultMatcher hasValidId() {
        return result -> {
            Long id = createdId(result);
            assertThat(id).isNotNull();
            assertThat(id).isPositive();
        };
    }

    protected ResultMatcher conformsToSchema(String schemaPath) {
        return result -> {
            jsonSchemaValidationService.validateOrError(
                result.getResponse().getContentAsString(),
                schemaPath
            );
        };
    }
}
