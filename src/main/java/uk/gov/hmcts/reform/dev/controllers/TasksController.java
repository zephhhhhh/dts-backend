package uk.gov.hmcts.reform.dev.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.models.TaskEntity;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

// -    Create a task with the following properties:
//          Title
//          Description (optional field)
//          Status
//          Due date/time
// X    Retrieve a task by ID
// X    Retrieve all tasks
// -    Update the status of a task
// -    Delete a task

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
    public ResponseEntity<TaskEntity> getTaskById(@PathVariable Long taskId) {
        log.debug(":GET:getTaskById: Fetching task with id: {}", taskId);

        return ok(taskService.getTaskById(taskId));
    }

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<TaskEntity>> getAllTasks() {
        log.debug(":GET:getAllTasks: fetching all tasks");

        return ok(taskService.getAllTasks());
    }

    @PostMapping(value = "/create")
    public ResponseEntity<TaskEntity> createNewTask(@RequestBody CreateTaskBody taskBody) {

        log.debug(":POST:createNewTask: creating a new task entity: {}", taskBody.toPrettyJson());

        // TODO: Implement this.
        return null;
    }

    // TODO: Unsure whether to use the status in the path or as a request body.
    @PatchMapping(value = "/update/{taskId}")
    public ResponseEntity<TaskEntity> updateTaskStatus(@PathVariable Long taskId) {
        log.debug(":PATCH:updateTaskStatus: updating task status with id: {}", taskId);

        // TODO: Implement this.
        return null;
    }

    @DeleteMapping(value = "/delete/{taskId}")
    public ResponseEntity<TaskEntity> deleteTask(@PathVariable Long taskId) {
        log.debug(":DELETE:deleteTask: deleting task with id: {}", taskId);

        // TODO: Implement this.
        return null;
    }
}
