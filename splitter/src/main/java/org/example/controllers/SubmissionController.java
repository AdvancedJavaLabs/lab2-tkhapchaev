package org.example.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.models.SubmissionActions;
import org.example.services.SubmissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@Tag(name = "Text files submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping(path = "/submit")
    public ResponseEntity<?> submitFile(@RequestBody SubmissionActions submissionActions) {
        try {
            var submissionInfo = submissionService.submitFile(submissionActions);

            return ResponseEntity.ok(submissionInfo);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }
}