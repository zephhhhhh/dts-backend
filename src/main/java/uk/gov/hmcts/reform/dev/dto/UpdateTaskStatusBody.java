package uk.gov.hmcts.reform.dev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskStatusBody implements ToJsonString {
    @JsonProperty("status")
    @NotNull
    private TaskStatus status;
}
