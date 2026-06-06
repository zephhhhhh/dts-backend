package uk.gov.hmcts.reform.dev.controllers;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.dto.CreateTaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusBody;
import uk.gov.hmcts.reform.dev.models.TaskEntity;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j(topic = "TasksControllerTest")
class TasksControllerTest {
    @Mock
    private TaskService taskService;

    @InjectMocks
    private TasksController tasksController;

    protected static final LocalDateTime TEST_TIME = LocalDateTime.parse("2026-06-05T19:32:30");

    @Test
    void getTaskById_Success() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        when(taskService.getTaskById(1L)).thenReturn(task);

        ResponseEntity<TaskEntity> response = tasksController.getTaskById(1L);
        assertNotNull(response);

        TaskEntity responseEntity = response.getBody();
        assertNotNull(responseEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(task, responseEntity);
        verify(taskService).getTaskById(1L);
    }

    @Test
    void getAllTasks_Success() {
        List<TaskEntity> taskList = getTaskEntities();
        when(taskService.getAllTasks()).thenReturn(taskList);

        ResponseEntity<List<TaskEntity>> response = tasksController.getAllTasks();
        assertNotNull(response);

        List<TaskEntity> responseTasks = response.getBody();
        assertNotNull(responseTasks);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(taskList.toArray(), responseTasks.toArray());
        verify(taskService).getAllTasks();
    }

    @Test
    void getAllTasks_noTasks_Success() {
        when(taskService.getAllTasks()).thenReturn(List.of());

        ResponseEntity<List<TaskEntity>> response = tasksController.getAllTasks();
        assertNotNull(response);

        List<TaskEntity> responseTasks = response.getBody();
        assertNotNull(responseTasks);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, responseTasks.size());
        verify(taskService).getAllTasks();
    }

    @Test
    void createNewTask_Success() {
        CreateTaskBody taskBody = new CreateTaskBody("Title", "",
                                                     TaskStatus.COMPLETED, TEST_TIME);

        when(taskService.createNewTask(taskBody)).thenReturn(42L);

        ResponseEntity<CreateTaskResponse> response = tasksController.createNewTask(taskBody);
        assertNotNull(response);

        CreateTaskResponse responseBody = response.getBody();
        assertNotNull(responseBody);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(42L, response.getBody().getId());
        verify(taskService).createNewTask(taskBody);
    }

    @Test
    void updateTaskStatus_Success() {
        TaskEntity task1 = new TaskEntity(1L, "Title 1", "Description",
                                          TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);
        UpdateTaskStatusBody updateTaskStatusBody = new UpdateTaskStatusBody(TaskStatus.TODO);

        when(taskService.updateTaskStatus(1L, updateTaskStatusBody)).thenReturn(task1);

        ResponseEntity<TaskEntity> response = tasksController.updateTaskStatus(1L, updateTaskStatusBody);
        assertNotNull(response);

        TaskEntity returnedEntity = response.getBody();
        assertNotNull(returnedEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task1, returnedEntity);
        verify(taskService).updateTaskStatus(1L, updateTaskStatusBody);
    }

    @Test
    void deleteTask_Success() {
        TaskEntity task1 = new TaskEntity(1L, "Title 1", "Description",
                                          TaskStatus.COMPLETED, TEST_TIME, TEST_TIME);

        when(taskService.deleteTask(1L)).thenReturn(task1);

        ResponseEntity<TaskEntity> response = tasksController.deleteTask(1L);
        assertNotNull(response);

        TaskEntity returnedEntity = response.getBody();
        assertNotNull(returnedEntity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(returnedEntity, task1);
        verify(taskService).deleteTask(1L);
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
}
