package uk.gov.hmcts.reform.dev.models;

import uk.gov.hmcts.reform.dev.dto.ToJsonString;

public enum TaskStatus implements ToJsonString {
    TODO,
    STARTED,
    COMPLETED
}
