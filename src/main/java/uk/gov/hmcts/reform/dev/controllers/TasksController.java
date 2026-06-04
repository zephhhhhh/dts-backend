package uk.gov.hmcts.reform.dev.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusBody;
import uk.gov.hmcts.reform.dev.models.TaskEntity;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

// X    Create a task with the following properties:
//          Title
//          Description (optional field)
//          Status
//          Due date/time
// X    Retrieve a task by ID
// X    Retrieve all tasks
// X    Update the status of a task
// X    Delete a task

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Slf4j(topic = "TasksController")
public class TasksController {
    private final TaskService taskService;

    @GetMapping(value = "/get-example-task", produces = "application/json")
    public ResponseEntity<TaskEntity> getExampleCase() {
        return ok(new TaskEntity(
            1L,
            "Example title",
            "Example description",
            "In progress",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1)
        ));
    }

    @GetMapping(value = "/{taskId}", produces = "application/json")
    public ResponseEntity<TaskEntity> getTaskById(
        @PathVariable Long taskId
    ) {
        log.debug(":GET:getTaskById: Fetching task with id: {}", taskId);

        return ok(taskService.getTaskById(taskId));
    }

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<TaskEntity>> getAllTasks() {
        log.debug(":GET:getAllTasks: fetching all tasks");

        return ok(taskService.getAllTasks());
    }

    @PostMapping(value = "/create")

    public ResponseEntity<Long> createNewTask(
        @RequestBody CreateTaskBody createTaskBody
    ) {

        log.debug(":POST:createNewTask: creating a new task entity: {}", createTaskBody.toPrettyJson());

        Long createdTaskId = taskService.createNewTask(createTaskBody);

        return ok(createdTaskId);
    }

    @PatchMapping(value = "/{taskId}")
    public ResponseEntity<TaskEntity> updateTaskStatus(
        @PathVariable Long taskId,
        @RequestBody UpdateTaskStatusBody newStatusBody
    ) {
        log.debug(":PATCH:updateTaskStatus: updating task status with id: {}, new status: {}",
                  taskId, newStatusBody.getStatus());

        TaskEntity updatedTask = taskService.updateTaskStatus(taskId, newStatusBody);

        return ok(updatedTask);
    }

    @DeleteMapping(value = "/{taskId}")
    public ResponseEntity<TaskEntity> deleteTask(
        @PathVariable Long taskId
    ) {
        log.debug(":DELETE:deleteTask: deleting task with id: {}", taskId);

        // TODO: Unsure on the convention of deleting entities,
        //       just going to return the deleted entity if it was deleted for now.

        TaskEntity deletedTask = taskService.deleteTask(taskId);

        return ok(deletedTask);
    }
}
