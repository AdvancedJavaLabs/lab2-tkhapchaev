package org.example.models;

import lombok.Data;

import java.nio.file.Files;
import java.nio.file.Paths;

@Data
public class SubmissionActions {
    private String submissionFilePath;
    private String resultFilePath;

    private boolean shouldCountWords;
    private boolean shouldTopWords;
    private boolean shouldAnalyzeSentiment;
    private boolean shouldReplaceNames;
    private boolean shouldSortSentencesByCharCount;

    private int topN;
    private String nameReplacement;

    private boolean shouldSortSentencesDescending;
    private boolean shouldSortSentencesPerSection;

    public void validate() {
        validateNameReplacement();
        validateTopN();
        validateSubmissionFilePath();
    }

    private void validateNameReplacement() {
        if (nameReplacement == null) {
            throw new IllegalStateException("Name replacement cannot be null");
        }

        if (nameReplacement.isBlank()) {
            throw new IllegalStateException("Name replacement cannot be blank");
        }
    }

    private void validateTopN() {
        if (topN <= 0) {
            throw new IllegalStateException("Top N must be greater than 0");
        }
    }

    private void validateSubmissionFilePath() {
        if (submissionFilePath == null) {
            throw new IllegalStateException("Submission file path cannot be null");
        }

        if (submissionFilePath.isBlank()) {
            throw new IllegalStateException("Submission file path cannot be blank");
        }

        if (!Files.exists(Paths.get(submissionFilePath))) {
            throw new IllegalStateException("Submission file does not exist");
        }

        if (!Files.isRegularFile(Paths.get(submissionFilePath))) {
            throw new IllegalStateException("Submission file is not a regular file");
        }

        if (!Files.isReadable(Paths.get(submissionFilePath))) {
            throw new IllegalStateException("Submission file is not readable");
        }
    }
}