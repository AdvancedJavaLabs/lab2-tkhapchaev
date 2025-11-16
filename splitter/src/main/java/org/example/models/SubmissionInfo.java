package org.example.models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SubmissionInfo {
    private UUID textId;

    private long fileSize;
    private long chunkCount;

    private SubmissionActions submissionActions;

    public SubmissionInfo(UUID textId, long fileSize, long chunkCount, SubmissionActions submissionActions) {
        if (textId == null) {
            throw new IllegalArgumentException("Text id cannot be null");
        }

        if (fileSize < 0) {
            throw new IllegalArgumentException("File size cannot be negative");
        }

        if (chunkCount < 0) {
            throw new IllegalArgumentException("Text chunks count cannot be negative");
        }

        if (submissionActions == null) {
            throw new IllegalArgumentException("Submission actions cannot be null");
        }

        this.textId = textId;

        this.fileSize = fileSize;
        this.chunkCount = chunkCount;

        this.submissionActions = submissionActions;
    }
}