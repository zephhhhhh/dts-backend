package uk.gov.hmcts.reform.dev.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.dto.CreateTaskBody;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusBody;
import uk.gov.hmcts.reform.dev.models.TaskEntity;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TaskService")
@Qualifier("taskService")
public class TaskService {
    private final TaskRepository repo;

    @Transactional(readOnly = true)
    public TaskEntity getTaskById(Long taskId) throws EntityNotFoundException {
        return repo.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
    }

    @Transactional(readOnly = true)
    public List<TaskEntity> getAllTasks() {
        return repo.findAll();
    }

    @Transactional
    public Long createNewTask(CreateTaskBody newTaskDesc) {
        log.debug(":createNewTask");

        TaskEntity newTask = new TaskEntity();
        newTask.setTitle(newTaskDesc.getTitle());
        newTask.setDescription(newTaskDesc.hasDescription() ? newTaskDesc.getDescription() : "");
        newTask.setStatus(newTaskDesc.getStatus());
        newTask.setCreatedDate(LocalDateTime.now());
        newTask.setDueDate(newTaskDesc.getDueDate());

        TaskEntity created = repo.save(newTask);

        return created.getId();
    }

    @Transactional
    public TaskEntity updateTaskStatus(Long taskId, UpdateTaskStatusBody newTaskStatus) throws EntityNotFoundException {
        // `getTaskById` will throw an exception if the task is not found, which will be propagated to the caller.
        TaskEntity taskToUpdate = getTaskById(taskId);
        taskToUpdate.setStatus(newTaskStatus.getStatus());

        return repo.save(taskToUpdate);
    }

    @Transactional
    public TaskEntity deleteTask(Long taskId) throws EntityNotFoundException {
        TaskEntity taskToDelete = getTaskById(taskId);
        repo.delete(taskToDelete);
        return taskToDelete;
    }
}
