package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.AggregatedResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

@Service
public class StorageService {
    private final ObjectMapper objectMapper;

    public StorageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void save(AggregatedResult aggregatedResult) throws IOException {
        if (aggregatedResult == null) {
            throw new IllegalArgumentException("Aggregated result cannot be null");
        }

        var submissionActions = aggregatedResult.getSubmissionInfo().getSubmissionActions();
        submissionActions.validate();

        var submittedAt = aggregatedResult.getSubmissionInfo().getSubmittedAt();
        var writtenAt = Instant.now();

        aggregatedResult.setTotalTimeMillis(Duration.between(submittedAt, writtenAt).toMillis());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(submissionActions.getResultFilePath()).toFile(), aggregatedResult);
    }
}