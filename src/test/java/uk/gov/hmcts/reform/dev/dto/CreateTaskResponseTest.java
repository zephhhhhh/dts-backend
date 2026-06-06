package uk.gov.hmcts.reform.dev.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@Slf4j(topic = "CreateTaskResponseTest")
class CreateTaskResponseTest {
    @Test
    void testEqualityAndHashCodeEquality() {
        final CreateTaskResponse a = new CreateTaskResponse(1L);
        final CreateTaskResponse b = new CreateTaskResponse(1L);

        final CreateTaskResponse idChanged = new CreateTaskResponse(2L);

        assertEquals(a, b);
        assertNotSame(a, b);
        assertNotEquals(a, idChanged);

        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), idChanged.hashCode());
    }

    @Test
    void testToString() {
        CreateTaskResponse a = new CreateTaskResponse(1L);
        assertNotNull(a.toString());
    }

    @Test
    void testToJsonString() throws JsonProcessingException {
        CreateTaskResponse a = new CreateTaskResponse(1L);

        String json = a.toJsonString();
        assertNotNull(json);
    }

    @Test
    void testFromJsonString() {
        String jsonSource = "{ \"id\": 1 }";

        CreateTaskResponse parsedBody = ToJsonString.toClassInstance(jsonSource, CreateTaskResponse.class);

        assertNotNull(parsedBody);
        assertEquals(1, parsedBody.getId());
    }
}
