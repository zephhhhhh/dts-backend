// In the rest of the HMCTS projects, this seems to be called '.entities' I think?
// Maybe want to rename this to entities to consistency reasons.

package uk.gov.hmcts.reform.dev.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Data
@Table(name = "tasks")
public class TaskEntity {
    // NOTE: Auto increment for now, potentially bad design (allows enumerating tasks), maybe use GUID?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    // NOTE: This is not in the spec, but in general having a created date is useful.
    @Column(name = "created_date")
    @JsonProperty("created_date")
    private LocalDateTime createdDate;

    @Column(name = "due_date")
    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }
}
