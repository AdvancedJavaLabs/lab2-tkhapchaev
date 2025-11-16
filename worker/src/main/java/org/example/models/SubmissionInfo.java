package org.example.models;

import lombok.Data;

import java.util.UUID;

@Data
public class SubmissionInfo {
    private UUID textId;

    private long fileSize;
    private long chunkCount;

    private SubmissionActions submissionActions;
}