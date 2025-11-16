package org.example.models;

import lombok.Data;

import java.io.IOException;
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

    public void validate() throws IOException {
        validateNameReplacement();
        validateTopN();
        validateResultFilePath();
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

    private void validateResultFilePath() throws IOException {
        if (resultFilePath == null) {
            throw new IllegalStateException("Result file path cannot be null");
        }

        if (resultFilePath.isBlank()) {
            throw new IllegalStateException("Result file path cannot be blank");
        }

        if (!Files.exists(Paths.get(resultFilePath))) {
            var parentDirectory = Paths.get(resultFilePath).getParent();

            if (parentDirectory != null && !Files.exists(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }

            Files.createFile(Paths.get(resultFilePath));
        }

        if (!Files.isRegularFile(Paths.get(resultFilePath))) {
            throw new IllegalStateException("Result file is not a regular file");
        }

        if (!Files.isWritable(Paths.get(resultFilePath))) {
            throw new IllegalStateException("Result file is not writable");
        }
    }
}