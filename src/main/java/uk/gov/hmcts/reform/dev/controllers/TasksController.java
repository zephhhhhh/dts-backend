package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Slf4j(topic = "TasksController")
@Tag(name = "Tasks Controller")
public class TasksController {
    private final TaskService taskService;

    @GetMapping(value = "/{taskId}", produces = "application/json")
    @Operation(
        summary = "Returns the task for a given taskId."
    )
    public ResponseEntity<TaskEntity> getTaskById(
        @PathVariable Long taskId
    ) {
        log.debug(":GET:getTaskById: Fetching task with id: {}", taskId);

        return ok(taskService.getTaskById(taskId));
    }

    @GetMapping(value = "/all", produces = "application/json")
    @Operation(
        summary = "Returns a collection of all the tasks."
    )
    public ResponseEntity<List<TaskEntity>> getAllTasks() {
        log.debug(":GET:getAllTasks: fetching all tasks");

        return ok(taskService.getAllTasks());
    }

    @PostMapping(value = "/")
    @Operation(
        summary = "Creates a new Task Entity in the DB based upon data in the request body.",
        description = "Returns the taskId of the task task that was created."
    )
    public ResponseEntity<Long> createNewTask(
        @RequestBody CreateTaskBody createTaskBody
    ) {

        log.debug(":POST:createNewTask: creating a new task entity: {}", createTaskBody.toPrettyJson());

        Long createdTaskId = taskService.createNewTask(createTaskBody);

        return ok(createdTaskId);
    }

    @PatchMapping(value = "/{taskId}")
    @Operation(
        summary = "Updates an existing Task Entity in the DB with a new status in the request body.",
        description = "Returns the updated state of the updated Task Entity, if an entity was updated."
    )
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
    @Operation(
        summary = "Deletes the Task for a given taskId.",
        description = "Returns the Task Entity that was deleted, if any entity was deleted."
    )
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
