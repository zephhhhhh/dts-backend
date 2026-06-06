/*
 * This code was re-used from other HMCTS repositories
 * Source: 'https://github.com/hmcts/opal-fines-service/'
 * File: '/src/test/java/uk/gov/hmcts/opal/controllers/advice/GlobalExceptionHandlerTest.java'
 * Credit: HMCTS
 */

package uk.gov.hmcts.reform.dev.controllers.advice;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.reform.dev.exception.JsonSchemaValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = GlobalExceptionHandler.class)
@Isolated
public class GlobalExceptionHandlerTest {
    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleMessageNotReadable_false() {
        HttpInputMessage msg = Mockito.mock(HttpInputMessage.class);
        ResponseEntity<ProblemDetail> r = globalExceptionHandler
            .handleHttpMessageNotReadableException(new HttpMessageNotReadableException("x", msg));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals(false, r.getBody().getProperties().get("retriable"));
    }

    @Test
    void handleEntityNotFound_false() {
        ResponseEntity<ProblemDetail> r = globalExceptionHandler
            .handleEntityNotFoundException(new EntityNotFoundException("nf"));
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals(false, r.getBody().getProperties().get("retriable"));
    }

    @Test
    void handleJsonSchema_false() {
        ResponseEntity<ProblemDetail> r = globalExceptionHandler
            .handleJsonSchemaValidationException(new JsonSchemaValidationException("bad schema"));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals(false, r.getBody().getProperties().get("retriable"));
    }

    @Test
    void handleIllegalArgument_false() {
        ResponseEntity<ProblemDetail> r = globalExceptionHandler
            .handleIllegalArgumentException(new IllegalArgumentException("bad arg"));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals(false, r.getBody().getProperties().get("retriable"));
    }
}
