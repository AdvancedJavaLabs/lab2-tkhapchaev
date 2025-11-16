package org.example.models;

import lombok.Data;

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
}