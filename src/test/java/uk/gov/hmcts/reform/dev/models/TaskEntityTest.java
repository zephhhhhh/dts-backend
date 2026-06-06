package uk.gov.hmcts.reform.dev.models;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@Slf4j(topic = "TaskEntityTest")
class TaskEntityTest {
    protected static final LocalDateTime TEST_TIME = LocalDateTime.parse("2026-06-05T19:32:30");
    protected static final LocalDateTime TEST_TIME_2 = LocalDateTime.parse("2026-06-06T19:32:30");

    @Test
    void testGettersAndSetters() {
        TaskEntity task = new TaskEntity();

        task.setId(1L);
        task.setTitle("Title");
        task.setDescription("Description");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDueDate(TEST_TIME);
        task.setCreatedDate(TEST_TIME_2);

        assertEquals(1L, task.getId());
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(TEST_TIME, task.getDueDate());
        assertEquals(TEST_TIME_2, task.getCreatedDate());
    }

    @Test
    void testLombokMethods() {
        final TaskEntity a = new TaskEntity(20L, "A", "B",
                                            TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);
        final TaskEntity b = new TaskEntity(20L, "A", "B",
                                            TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);
        final TaskEntity idChanged = new TaskEntity(30L, "A", "B",
                                            TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);
        final TaskEntity titleChanged = new TaskEntity(20L, "a", "B",
                                                    TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);
        final TaskEntity descriptionChanged = new TaskEntity(20L, "A", "b",
                                                       TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);
        final TaskEntity statusChanged = new TaskEntity(20L, "A", "B",
                                                             TaskStatus.TODO, TEST_TIME, TEST_TIME);
        final TaskEntity createdChanged = new TaskEntity(20L, "A", "B",
                                                        TaskStatus.COMPLETED, TEST_TIME_2, TEST_TIME);
        final TaskEntity dueChanged = new TaskEntity(20L, "A", "B",
                                                         TaskStatus.COMPLETED, TEST_TIME, TEST_TIME_2);

        assertEquals(a, b);
        assertNotSame(a, b);
        assertNotEquals(a, idChanged);
        assertNotEquals(a, titleChanged);
        assertNotEquals(a, descriptionChanged);
        assertNotEquals(a, statusChanged);
        assertNotEquals(a, createdChanged);
        assertNotEquals(a, dueChanged);

        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), idChanged.hashCode());
        assertNotEquals(a.hashCode(), titleChanged.hashCode());
        assertNotEquals(a.hashCode(), descriptionChanged.hashCode());
        assertNotEquals(a.hashCode(), statusChanged.hashCode());
        assertNotEquals(a.hashCode(), createdChanged.hashCode());
        assertNotEquals(a.hashCode(), dueChanged.hashCode());

        assertNotNull(a.toString());
    }
}
