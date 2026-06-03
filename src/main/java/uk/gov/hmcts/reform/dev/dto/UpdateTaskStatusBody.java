package uk.gov.hmcts.reform.dev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskStatusBody implements ToJsonString {
    @JsonProperty("status")
    @NotNull
    @Size(min = 1)
    private String status;
}
