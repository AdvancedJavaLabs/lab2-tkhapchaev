package org.example.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProcessingResult {
    private Long wordCount;

    private Map<String, Long> topWords;
    private Map<String, Long> sortedSentences;

    private String sentiment;

    private List<String> contentWithReplacedNames;
}