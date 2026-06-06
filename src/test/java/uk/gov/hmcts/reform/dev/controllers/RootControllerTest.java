package uk.gov.hmcts.reform.dev.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@Slf4j(topic = "RootControllerTest")
class RootControllerTest {
    private final RootController rootController = new RootController();

    @Test
    void welcome_Success() {
        ResponseEntity<String> response = rootController.welcome();
        assertNotNull(response);

        String responseBody = response.getBody();
        assertNotNull(responseBody);

        assertEquals("Welcome to test-backend", responseBody);
    }
}
