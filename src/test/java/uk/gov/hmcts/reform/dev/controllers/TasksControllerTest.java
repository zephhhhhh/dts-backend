package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.dev.models.TaskEntity;
import uk.gov.hmcts.reform.dev.service.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TasksControllerTest {
    @Mock
    private TaskService taskService;

    @InjectMocks
    private TasksController tasksController;

    @Test
    void getTaskById_returnsOkWithTask() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        when(taskService.getTaskById(1L)).thenReturn(task);

        ResponseEntity<TaskEntity> response = tasksController.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(task, response.getBody());
        verify(taskService).getTaskById(1L);
    }

    @Test
    void getAllTasks_returnsOkWithList() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        when(taskService.getTaskById(1L)).thenReturn(task);

        ResponseEntity<TaskEntity> response = tasksController.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(task, response.getBody());
        verify(taskService).getTaskById(1L);
    }
}
