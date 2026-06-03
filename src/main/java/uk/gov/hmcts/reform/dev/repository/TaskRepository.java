package uk.gov.hmcts.reform.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.dev.models.TaskEntity;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Override
    Optional<TaskEntity> findById(Long taskId);
}
