package uk.gov.hmcts.reform.dev.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j(topic = "CreateTaskBodyTest")
class CreateTaskBodyTest {
    protected static final LocalDateTime TEST_TIME = LocalDateTime.parse("2026-06-05T19:32:30");

    @Test
    void testHasDescription_valid_returnsTrue() {
        CreateTaskBody validBody = new CreateTaskBody("Correct", "Description",
                                                      TaskStatus.COMPLETED, TEST_TIME);
        assertTrue(validBody.hasDescription());
    }

    @Test
    void testHasDescription_null_returnsFalse() {
        CreateTaskBody validBody = new CreateTaskBody("Correct", null,
                                                      TaskStatus.COMPLETED, TEST_TIME);
        assertFalse(validBody.hasDescription());
    }

    @Test
    void testHasDescription_emptyString_returnsFalse() {
        CreateTaskBody validBody = new CreateTaskBody("Correct", "",
                                                      TaskStatus.COMPLETED, TEST_TIME);
        assertFalse(validBody.hasDescription());
    }

    @Test
    void testHasDescription_whitespaceString_returnsFalse() {
        CreateTaskBody validBody = new CreateTaskBody("Correct", "      ",
                                                      TaskStatus.COMPLETED, TEST_TIME);
        assertFalse(validBody.hasDescription());
    }

    @Test
    void testEqualityAndHashCodeEquality() {
        final CreateTaskBody a = new CreateTaskBody("Correct", "Description",
                                                      TaskStatus.COMPLETED, TEST_TIME);
        final CreateTaskBody b = new CreateTaskBody("Correct", "Description",
                                                      TaskStatus.COMPLETED, TEST_TIME);

        final CreateTaskBody titleChanged = new CreateTaskBody("correct", "Description",
                                              TaskStatus.COMPLETED, TEST_TIME);
        final CreateTaskBody descriptionChanged = new CreateTaskBody("Correct", "description",
                                              TaskStatus.COMPLETED, TEST_TIME);
        final CreateTaskBody statusChanged = new CreateTaskBody("Correct", "Description",
                                                               TaskStatus.TODO, TEST_TIME);
        final CreateTaskBody timeChanged = new CreateTaskBody("Correct", "Description",
                                                          TaskStatus.TODO, TEST_TIME);

        assertEquals(a, b);
        assertNotSame(a, b);
        assertNotEquals(a, titleChanged);
        assertNotEquals(a, descriptionChanged);
        assertNotEquals(a, statusChanged);
        assertNotEquals(a, timeChanged);

        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), titleChanged.hashCode());
        assertNotEquals(a.hashCode(), descriptionChanged.hashCode());
        assertNotEquals(a.hashCode(), statusChanged.hashCode());
        assertNotEquals(a.hashCode(), timeChanged.hashCode());
    }

    @Test
    void testToString() {
        CreateTaskBody a = new CreateTaskBody("Correct", "Description",
                                              TaskStatus.COMPLETED, TEST_TIME);
        assertNotNull(a.toString());
    }

    @Test
    void testToJsonString() throws JsonProcessingException {
        CreateTaskBody a = new CreateTaskBody("Correct", "Description",
                                              TaskStatus.COMPLETED, TEST_TIME);
        String json = a.toJsonString();
        assertNotNull(json);
    }

    @Test
    void testFromJsonString() {
        String jsonSource = "{\"title\": \"A\", "
            + "\"description\": \"B\", "
            + "\"status\": \"TODO\", "
            + "\"due_date\": \"2026-06-05T19:32:30\"}";

        CreateTaskBody parsedBody = ToJsonString.toClassInstance(jsonSource, CreateTaskBody.class);

        assertNotNull(parsedBody);
        assertEquals("A", parsedBody.getTitle());
        assertEquals("B", parsedBody.getDescription());
        assertEquals(TaskStatus.TODO, parsedBody.getStatus());
        assertEquals(TEST_TIME, parsedBody.getDueDate());
    }
}
