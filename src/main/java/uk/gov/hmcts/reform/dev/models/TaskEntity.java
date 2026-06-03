// In the rest of the HMCTS projects, this seems to be called '.entities' I think?
// Maybe want to rename this to entities to consistency reasons.

package uk.gov.hmcts.reform.dev.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id
    private Long id;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // TODO: My gut is telling me to make this an enum, but it is unclear if status may be user defined
    //       and not just 'in progress' and 'completed'
    @Column(name = "status")
    private String status;

    // TODO: This is not in the spec, but in general having a created date is useful.
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;
}
