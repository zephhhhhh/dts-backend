package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@Tag(name = "Root Controller")
public class RootController {
    @GetMapping("/")
    @Operation(summary = "Health test endpoint, prints a welcome message.")
    public ResponseEntity<String> welcome() {
        return ok("Welcome to test-backend");
    }
}
