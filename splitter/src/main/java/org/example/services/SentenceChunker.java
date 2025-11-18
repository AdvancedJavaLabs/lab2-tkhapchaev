package org.example.services;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.example.models.Chunk;
import org.example.utilities.SentenceDetectionHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SentenceChunker {
    private final SentenceDetectorME sentenceDetector;

    @Value("${app.chunker.max-sentences-per-chunk}")
    private long maxSentencesPerChunk;

    public SentenceChunker() throws IOException {
        var sentenceModel = getClass().getResourceAsStream("/models/en-sentence.bin");

        if (sentenceModel == null) {
            throw new RuntimeException("Unable to load OpenNLP sentence model");
        }

        sentenceDetector = new SentenceDetectorME(new SentenceModel(sentenceModel));
    }

    public List<Chunk> chunk(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        if (text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be blank");
        }

        if (maxSentencesPerChunk <= 0) {
            throw new IllegalArgumentException("Max sentences per chunk cannot be less than 1");
        }

        var chunks = new ArrayList<Chunk>();
        var chunkId = 0;

        var detectedSpans = sentenceDetector.sentPosDetect(text);
        var currentChunkSentences = new ArrayList<String>();
        var buffer = new StringBuilder();

        for (var detectedSpan : detectedSpans) {
            var sentence = text.substring(detectedSpan.getStart(), detectedSpan.getEnd()).trim();

            if (sentence.isEmpty()) {
                continue;
            }

            var endsWithAbbreviation = false;

            for (var abbreviation : SentenceDetectionHelper.abbreviations) {
                if (sentence.toLowerCase().endsWith(abbreviation)) {
                    endsWithAbbreviation = true;

                    break;
                }
            }

            if (endsWithAbbreviation) {
                if (!buffer.isEmpty()) {
                    buffer.append(" ");
                }

                buffer.append(sentence);
            } else {
                if (!buffer.isEmpty()) {
                    sentence = buffer + " " + sentence;
                    buffer.setLength(0);
                }

                currentChunkSentences.add(sentence);
            }

            if (currentChunkSentences.size() >= maxSentencesPerChunk) {
                chunks.add(new Chunk(chunkId, new ArrayList<>(currentChunkSentences)));
                currentChunkSentences.clear();
                chunkId++;
            }
        }

        if (!buffer.isEmpty()) {
            currentChunkSentences.add(buffer.toString());
        }

        if (!currentChunkSentences.isEmpty()) {
            chunks.add(new Chunk(chunkId, new ArrayList<>(currentChunkSentences)));
        }

        return chunks;
    }
}