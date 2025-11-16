package org.example.services;

import org.example.models.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ChunkAggregator {
    private final Map<UUID, List<Chunk>> chunksByTextId;

    private final KafkaProducer kafkaProducer;

    public ChunkAggregator(KafkaProducer kafkaProducer) {
        this.chunksByTextId = new ConcurrentHashMap<>();
        this.kafkaProducer = kafkaProducer;
    }

    public void addProcessedChunkAndTryPublish(Chunk processedChunk) {
        if (processedChunk == null) {
            throw new IllegalArgumentException("Processed chunk cannot be null");
        }

        var textId = processedChunk.getSubmissionInfo().getTextId();
        var totalChunks = processedChunk.getSubmissionInfo().getChunkCount();

        chunksByTextId.compute(textId, (id, chunks) -> {
            if (chunks == null) {
                chunks = new ArrayList<>();
            }

            chunks.add(processedChunk);

            return chunks;
        });

        List<Chunk> currentChunks = chunksByTextId.get(textId);
        currentChunks.sort(Comparator.comparingLong(Chunk::getId));

        if (currentChunks.size() >= totalChunks) {
            var aggregatedResult = aggregate(currentChunks);
            kafkaProducer.publishAggregatedResult(aggregatedResult);
            chunksByTextId.remove(textId);
        }
    }

    private AggregatedResult aggregate(List<Chunk> chunks) {
        if (chunks == null) {
            throw new IllegalArgumentException("Chunks cannot be null");
        }

        if (chunks.isEmpty()) {
            throw new IllegalArgumentException("Chunks cannot be empty");
        }

        var aggregatedProcessingResult = new ProcessingResult();
        var submissionInfo = chunks.getFirst().getSubmissionInfo();
        var submissionActions = submissionInfo.getSubmissionActions();

        if (submissionActions.isShouldCountWords()) {
            var totalWords = chunks.stream()
                    .map(Chunk::getProcessingResult)
                    .filter(Objects::nonNull)
                    .map(ProcessingResult::getWordCount)
                    .filter(Objects::nonNull)
                    .mapToLong(Long::longValue)
                    .sum();

            aggregatedProcessingResult.setWordCount(totalWords);
        }

        if (submissionActions.isShouldTopWords()) {
            Map<String, Long> topWordsMerged = new HashMap<>();

            for (var chunk : chunks) {
                var processingResult = chunk.getProcessingResult();

                if (processingResult != null && processingResult.getTopWords() != null) {
                    processingResult.getTopWords().forEach((word, count) -> topWordsMerged.merge(word, count, Long::sum));
                }
            }

            int topN = submissionActions.getTopN();

            Map<String, Long> topNWords = topWordsMerged.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(topN)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (i, j) -> i,
                            LinkedHashMap::new
                    ));

            aggregatedProcessingResult.setTopWords(topNWords);
        }

        if (submissionActions.isShouldAnalyzeSentiment()) {
            Map<String, Long> sentimentCount = new HashMap<>();

            for (var chunk : chunks) {
                var processingResult = chunk.getProcessingResult();

                if (processingResult != null && processingResult.getSentiment() != null) {
                    sentimentCount.merge(processingResult.getSentiment(), 1L, Long::sum);
                }
            }

            String majoritySentiment = sentimentCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("neutral");

            aggregatedProcessingResult.setSentiment(majoritySentiment);
        }

        if (submissionActions.isShouldReplaceNames()) {
            var contentWithReplacedNamesCombined = chunks.stream()
                    .map(Chunk::getProcessingResult)
                    .filter(Objects::nonNull)
                    .map(ProcessingResult::getContentWithReplacedNames)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            aggregatedProcessingResult.setContentWithReplacedNames(contentWithReplacedNamesCombined);
        }

        if (submissionActions.isShouldSortSentencesByCharCount()) {
            Map<String, Long> combinedSentences = new LinkedHashMap<>();

            if (submissionActions.isShouldSortSentencesPerSection()) {
                for (var chunk : chunks) {
                    var processingResult = chunk.getProcessingResult();

                    if (processingResult != null && processingResult.getSortedSentences() != null) {
                        combinedSentences.putAll(processingResult.getSortedSentences());
                    }
                }

                aggregatedProcessingResult.setSortedSentences(combinedSentences);
            } else {
                for (var chunk : chunks) {
                    var processingResult = chunk.getProcessingResult();

                    if (processingResult != null && processingResult.getSortedSentences() != null) {
                        combinedSentences.putAll(processingResult.getSortedSentences());
                    }
                }

                var descendingOrder = submissionActions.isShouldSortSentencesDescending();

                Map<String, Long> sortedSentences = combinedSentences.entrySet().stream()
                        .sorted((i, j) -> descendingOrder
                                ? Long.compare(j.getValue(), i.getValue())
                                : Long.compare(i.getValue(), j.getValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (i, j) -> i,
                                LinkedHashMap::new
                        ));

                aggregatedProcessingResult.setSortedSentences(sortedSentences);
            }
        }

        var aggregatedResult = new AggregatedResult();
        aggregatedResult.setProcessingResult(aggregatedProcessingResult);
        aggregatedResult.setSubmissionInfo(submissionInfo);

        return aggregatedResult;
    }
}