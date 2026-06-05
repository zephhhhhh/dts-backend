package uk.gov.hmcts.reform.dev.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateTaskResponse implements ToJsonString {
    @NotNull
    private Long id;
}
