package org.example.models;

import lombok.Data;

import java.util.List;

@Data
public class Chunk {
    private long id;

    private List<String> content;

    private SubmissionInfo submissionInfo;
    private ProcessingResult processingResult;
}