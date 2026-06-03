package uk.gov.hmcts.reform.dev.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.models.TaskEntity;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repo;

    public TaskEntity getTaskById(Long taskId) {
        return repo.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));
    }

    public List<TaskEntity> getAllTasks() {
        return repo.findAll();
    }
}
