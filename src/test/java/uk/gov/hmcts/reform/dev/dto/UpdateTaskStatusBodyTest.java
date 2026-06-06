package uk.gov.hmcts.reform.dev.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@Slf4j(topic = "UpdateTaskStatusBodyTest")
class UpdateTaskStatusBodyTest {
    @Test
    void testEqualityAndHashCodeEquality() {
        final UpdateTaskStatusBody a = new UpdateTaskStatusBody(TaskStatus.STARTED);
        final UpdateTaskStatusBody b = new UpdateTaskStatusBody(TaskStatus.STARTED);

        final UpdateTaskStatusBody statusChanged = new UpdateTaskStatusBody(TaskStatus.COMPLETED);

        assertEquals(a, b);
        assertNotSame(a, b);
        assertNotEquals(a, statusChanged);

        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), statusChanged.hashCode());
    }

    @Test
    void testToString() {
        UpdateTaskStatusBody a = new UpdateTaskStatusBody(TaskStatus.COMPLETED);
        assertNotNull(a.toString());
    }

    @Test
    void testToJsonString() throws JsonProcessingException {
        UpdateTaskStatusBody a = new UpdateTaskStatusBody(TaskStatus.COMPLETED);

        String json = a.toJsonString();
        assertNotNull(json);
    }

    @Test
    void testFromJsonString() {
        String jsonSource = "{ \"status\": \"TODO\" }";

        UpdateTaskStatusBody parsedBody = ToJsonString.toClassInstance(jsonSource, UpdateTaskStatusBody.class);

        assertNotNull(parsedBody);
        assertEquals(TaskStatus.TODO, parsedBody.getStatus());
    }
}
