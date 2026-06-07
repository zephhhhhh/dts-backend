package uk.gov.hmcts.reform.dev.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateTaskBody implements ToJsonString {
    @JsonProperty("title")
    @NotNull
    @Size(min = 1)
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    @NotNull
    private TaskStatus status;

    @JsonProperty("due_date")
    @NotNull
    private LocalDateTime dueDate;

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }
}
