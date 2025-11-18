package org.example.services;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.example.models.Chunk;
import org.example.models.ProcessingResult;
import org.example.utilities.SentimentAnalysisHelper;
import org.example.utilities.TopWordsHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChunkProcessor {
    private final KafkaProducer kafkaProducer;

    private final TokenizerME tokenizer;
    private final NameFinderME nameFinder;

    private final String wordRegex = "[\\w'@-]+";

    public ChunkProcessor(KafkaProducer kafkaProducer) throws IOException {
        this.kafkaProducer = kafkaProducer;

        var nameModel = getClass().getResourceAsStream("/models/en-ner.bin");

        if (nameModel == null) {
            throw new RuntimeException("Unable to load OpenNLP NER model");
        }

        nameFinder = new NameFinderME(new TokenNameFinderModel(nameModel));

        var tokenModel = getClass().getResourceAsStream("/models/en-token.bin");

        if (tokenModel == null) {
            throw new RuntimeException("Unable to load OpenNLP token model");
        }

        tokenizer = new TokenizerME(new TokenizerModel(tokenModel));
    }

    public void process(Chunk submittedChunk) {
        if (submittedChunk == null) {
            throw new IllegalArgumentException("Chunk cannot be null");
        }

        var submissionActions = submittedChunk.getSubmissionInfo().getSubmissionActions();
        var processingResult = new ProcessingResult();

        var text = String.join(" ", submittedChunk.getContent());
        var tokens = Arrays.stream(tokenizer.tokenize(text)).map(String::toLowerCase).toArray(String[]::new);

        if (submissionActions.isShouldCountWords()) {
            var wordCount = Arrays.stream(tokens).filter(token -> token.matches(wordRegex)).count();
            processingResult.setWordCount(wordCount);
        }

        if (submissionActions.isShouldTopWords()) {
            Map<String, Long> wordFrequency = new HashMap<>();

            for (var token : tokens) {
                if (token.matches(wordRegex) && !Arrays.asList(TopWordsHelper.stopWords).contains(token)) {
                    wordFrequency.put(token, wordFrequency.getOrDefault(token, 0L) + 1);
                }
            }

            Map<String, Long> sortedTopWords = wordFrequency.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(submissionActions.getTopN())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (i, j) -> i,
                            LinkedHashMap::new
                    ));

            processingResult.setTopWords(sortedTopWords);
        }

        if (submissionActions.isShouldAnalyzeSentiment()) {
            var score = 0;

            for (var token : tokens) {
                if (Arrays.asList(SentimentAnalysisHelper.positiveWords).contains(token)) {
                    score++;
                } else if (Arrays.asList(SentimentAnalysisHelper.negativeWords).contains(token)) {
                    score--;
                }
            }

            if (score > 0) {
                processingResult.setSentiment("positive");
            } else if (score < 0) {
                processingResult.setSentiment("negative");
            } else {
                processingResult.setSentiment("neutral");
            }
        }

        if (submissionActions.isShouldReplaceNames()) {
            var sentencesWithReplacedNames = new ArrayList<String>();

            for (var sentence : submittedChunk.getContent()) {
                var sentenceTokens = tokenizer.tokenize(sentence);
                var nameSpans = nameFinder.find(sentenceTokens);

                for (var nameSpan : nameSpans) {
                    for (int i = nameSpan.getStart(); i < nameSpan.getEnd(); i++) {
                        sentenceTokens[i] = submissionActions.getNameReplacement();
                    }
                }

                sentencesWithReplacedNames.add(String.join(" ", sentenceTokens));
            }

            processingResult.setContentWithReplacedNames(sentencesWithReplacedNames);
        }

        if (submissionActions.isShouldSortSentencesByCharCount()) {
            var sentenceMap = new LinkedHashMap<String, Long>();

            submittedChunk.getContent().stream()
                    .sorted((sentence1, sentence2) -> {
                        var cmp = Integer.compare(sentence1.length(), sentence2.length());

                        return submissionActions.isShouldSortSentencesDescending() ? -cmp : cmp;
                    })
                    .forEach(sentence -> sentenceMap.put(sentence, (long) sentence.length()));

            processingResult.setSortedSentences(sentenceMap);
        }

        submittedChunk.setProcessingResult(processingResult);
        kafkaProducer.publishProcessedChunk(submittedChunk);
    }
}