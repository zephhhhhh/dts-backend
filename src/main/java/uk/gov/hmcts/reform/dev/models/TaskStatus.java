package uk.gov.hmcts.reform.dev.models;

import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.dev.dto.ToJsonString;

@NoArgsConstructor
public enum TaskStatus implements ToJsonString {
    TODO,
    STARTED,
    COMPLETED
}
