package org.example.models;

import lombok.Data;

@Data
public class AggregatedResult {
    private ProcessingResult processingResult;
    private SubmissionInfo submissionInfo;
}