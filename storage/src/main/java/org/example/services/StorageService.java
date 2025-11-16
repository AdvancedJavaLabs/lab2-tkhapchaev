package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.AggregatedResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;

@Service
public class StorageService {
    public void save(AggregatedResult aggregatedResult) throws IOException {
        if (aggregatedResult == null) {
            throw new IllegalArgumentException("Aggregated result cannot be null");
        }

        var mapper = new ObjectMapper();
        var submissionActions = aggregatedResult.getSubmissionInfo().getSubmissionActions();

        submissionActions.validate();
        mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(submissionActions.getResultFilePath()).toFile(), aggregatedResult);
    }
}