package uk.gov.hmcts.reform.dev.controllers.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j(topic = "GlobalExceptionHandler")
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private ResponseEntity<ProblemDetail> responseWithProblemDetail(
        HttpStatus status, ProblemDetail problemDetail
    ) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON);
        return builder.body(problemDetail);
    }

    private ProblemDetail createProblemDetail(
        HttpStatus status, String title, String detail, boolean retry
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("retriable", retry);
        return problemDetail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(
        EntityNotFoundException entityNotFoundException) {

        ProblemDetail problemDetail = createProblemDetail(
            HttpStatus.NOT_FOUND,
            "Entity Not Found",
            "The requested entity could not be found",
            false
        );

        return responseWithProblemDetail(HttpStatus.NOT_FOUND, problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException e) {
        ProblemDetail problemDetail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            "Invalid arguments were provided in the request",
            false
        );
        return responseWithProblemDetail(HttpStatus.BAD_REQUEST, problemDetail);
    }
}
