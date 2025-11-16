package org.example.models;

import lombok.Getter;

import java.util.List;

@Getter
public class Chunk {
    private long id;

    private List<String> content;

    private SubmissionInfo submissionInfo;

    public Chunk(long id, List<String> content) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative");
        }

        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        if (content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        this.id = id;

        this.content = content;
    }

    public void setSubmissionInfo(SubmissionInfo submissionInfo) {
        if (submissionInfo == null) {
            throw new IllegalArgumentException("Submission info cannot be null");
        }

        this.submissionInfo = submissionInfo;
    }
}