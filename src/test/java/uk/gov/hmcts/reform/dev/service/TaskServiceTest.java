package uk.gov.hmcts.reform.dev.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusBody;
import uk.gov.hmcts.reform.dev.models.TaskEntity;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j(topic = "TaskServiceTest")
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepo;

    @InjectMocks
    private TaskService taskService;

    protected static final LocalDateTime TEST_TIME = LocalDateTime.parse("2026-06-05T19:32:30");

    void validateCreatedTaskEntity(CreateTaskBody spec, TaskEntity created) {
        assertNotNull(created);
        assertNotNull(created.getDescription());
        assertEquals(spec.getTitle(), created.getTitle());
        assertEquals(spec.getStatus(), created.getStatus());
        assertEquals(spec.getDueDate(), created.getDueDate());
        assertNotNull(created.getCreatedDate());
    }

    @Test
    void getTaskById_taskExists_returnsTask() {
        TaskEntity existing = new TaskEntity(1L, "Title", "Description",
                                             TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(existing));

        TaskEntity result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(result, existing);
        verify(taskRepo).findById(1L);
    }

    @Test
    void getTaskById_noTask_throwsEntityNotFound() {
        when(taskRepo.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> taskService.getTaskById(Long.MAX_VALUE)
        );

        assertEquals("Task not found with id: " + Long.MAX_VALUE, ex.getMessage());
        verify(taskRepo).findById(Long.MAX_VALUE);
    }

    @Test
    void getAllTasks_emptyTasks_returnsEmptyList() {
        List<TaskEntity> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(0, tasks.size());

        verify(taskRepo).findAll();
    }

    @Test
    void getAllTasks_hasTasks_returnsAllTasks() {
        // TODO: there has got to be a better way to do this
        List<TaskEntity> sourceList = getTaskEntities();

        when(taskRepo.findAll()).thenReturn(sourceList);

        List<TaskEntity> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(sourceList.size(), tasks.size());
        assertArrayEquals(sourceList.toArray(), tasks.toArray());

        verify(taskRepo).findAll();
    }

    private static @NonNull List<TaskEntity> getTaskEntities() {
        TaskEntity task1 = new TaskEntity(1L, "Title 1", "Description",
                                             TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);
        TaskEntity task2 = new TaskEntity(2L, "Title 2", "Description",
                                             TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);
        TaskEntity task3 = new TaskEntity(3L, "Title 3", "Description",
                                             TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);

        return Arrays.asList(task1, task2, task3);
    }

    @Test
    void createTask_validTaskCreation_createTaskNormally() {
        CreateTaskBody taskBody = new CreateTaskBody("Title", "Description",
                                                     TaskStatus.COMPLETED, TEST_TIME);

        TaskEntity persisted = new TaskEntity();
        persisted.setId(42L);
        when(taskRepo.save(any(TaskEntity.class))).thenReturn(persisted);

        Long newId = taskService.createNewTask(taskBody);

        assertEquals(42L, newId);

        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepo).save(captor.capture());
        TaskEntity saved = captor.getValue();

        validateCreatedTaskEntity(taskBody, saved);
        assertEquals(taskBody.getDescription(), saved.getDescription());
    }

    @Test
    void createTask_blankDescription_createsTaskNormally() {
        CreateTaskBody taskBody = new CreateTaskBody("Title", "",
                                                     TaskStatus.COMPLETED, TEST_TIME);

        TaskEntity persisted = new TaskEntity();
        persisted.setId(42L);
        when(taskRepo.save(any(TaskEntity.class))).thenReturn(persisted);

        Long newId = taskService.createNewTask(taskBody);

        assertEquals(42L, newId);

        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepo).save(captor.capture());
        TaskEntity saved = captor.getValue();

        validateCreatedTaskEntity(taskBody, saved);
        assertEquals(taskBody.getDescription(), saved.getDescription());
    }

    @Test
    void createTask_whitespaceDescription_createsTaskWithEmptyDescription() {
        CreateTaskBody taskBody = new CreateTaskBody("Title", "         ",
                                                     TaskStatus.COMPLETED, TEST_TIME);

        TaskEntity persisted = new TaskEntity();
        persisted.setId(42L);
        when(taskRepo.save(any(TaskEntity.class))).thenReturn(persisted);

        Long newId = taskService.createNewTask(taskBody);

        assertEquals(42L, newId);

        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepo).save(captor.capture());
        TaskEntity saved = captor.getValue();

        validateCreatedTaskEntity(taskBody, saved);
        assertEquals("", saved.getDescription());
    }

    @Test
    void createTask_nullDescription_createsTaskWithEmptyDescription() {
        CreateTaskBody taskBody = new CreateTaskBody("Title", null,
                                                     TaskStatus.COMPLETED, TEST_TIME);

        TaskEntity persisted = new TaskEntity();
        persisted.setId(42L);
        when(taskRepo.save(any(TaskEntity.class))).thenReturn(persisted);

        Long newId = taskService.createNewTask(taskBody);

        assertEquals(42L, newId);

        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepo).save(captor.capture());
        TaskEntity saved = captor.getValue();

        validateCreatedTaskEntity(taskBody, saved);
        assertEquals("", saved.getDescription());
    }

    @Test
    void updateTaskStatus_validStatus_updatesTaskAndSaves_returnsUpdated() {
        TaskEntity task1 = new TaskEntity(1L, "Title 1", "Description",
                                          TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepo.save(any(TaskEntity.class))).thenReturn(task1);

        UpdateTaskStatusBody body = new UpdateTaskStatusBody(TaskStatus.TODO);

        final TaskEntity result = taskService.updateTaskStatus(1L, body);

        verify(taskRepo).findById(1L);

        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepo).save(captor.capture());
        TaskEntity saved = captor.getValue();

        assertSame(task1, saved);
        assertEquals(TaskStatus.TODO, saved.getStatus());
        assertEquals(saved, result);
    }

    @Test
    void updateTaskStatus_invalidTask_throwsAndDoesNotModify() {
        when(taskRepo.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> taskService.updateTaskStatus(Long.MAX_VALUE, new UpdateTaskStatusBody(TaskStatus.TODO))
        );

        assertEquals("Task not found with id: " + Long.MAX_VALUE, ex.getMessage());

        verify(taskRepo).findById(Long.MAX_VALUE);
        verify(taskRepo, never()).save(any());
    }

    @Test
    void deleteTaskById_validTask_removesTask_returnsRemovedTask() {
        TaskEntity task1 = new TaskEntity(1L, "Title 1", "Description",
                                          TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task1));

        TaskEntity deletedEntity = taskService.deleteTask(1L);

        verify(taskRepo).findById(1L);
        verify(taskRepo).delete(task1);
        assertSame(task1, deletedEntity);
    }

    @Test
    void deleteTaskById_invalidTask_throws() {
        when(taskRepo.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> taskService.deleteTask(Long.MAX_VALUE)
        );

        assertEquals("Task not found with id: " + Long.MAX_VALUE, ex.getMessage());

        verify(taskRepo).findById(Long.MAX_VALUE);
        verify(taskRepo, never()).delete(any());
    }
}
