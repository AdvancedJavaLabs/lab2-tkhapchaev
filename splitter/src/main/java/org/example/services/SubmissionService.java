package org.example.services;

import org.example.models.SubmissionActions;
import org.example.models.SubmissionInfo;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class SubmissionService {
    private SentenceChunker sentenceChunker;
    private KafkaProducer kafkaProducer;

    public SubmissionService(SentenceChunker sentenceChunker, KafkaProducer kafkaProducer) {
        this.sentenceChunker = sentenceChunker;
        this.kafkaProducer = kafkaProducer;
    }

    public SubmissionInfo submitFile(SubmissionActions submissionActions) throws IOException {
        if (submissionActions == null) {
            throw new IllegalArgumentException("Processing actions cannot be null");
        }

        submissionActions.validate();
        byte[] content = readFileContent(submissionActions.getSubmissionFilePath());

        var textId = UUID.randomUUID();
        var text = convertFileContentToString(content);

        var chunks = sentenceChunker.chunk(text);

        if (chunks.isEmpty()) {
            throw new IllegalArgumentException("Cannot split multipart file into chunks");
        }

        var submissionInfo = new SubmissionInfo(textId, content.length, chunks.size(), submissionActions);

        for (var chunk : chunks) {
            chunk.setSubmissionInfo(submissionInfo);
        }

        kafkaProducer.publishSubmittedChunks(chunks);

        return submissionInfo;
    }

    private byte[] readFileContent(String submissionFilePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Path.of(submissionFilePath))) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            return buffer.toByteArray();
        }
    }

    private String convertFileContentToString(byte[] fileBytes) {
        var encoding = detectEncoding(fileBytes);

        return new String(fileBytes, java.nio.charset.Charset.forName(encoding));
    }

    private String detectEncoding(byte[] fileBytes) {
        var universalDetector = new UniversalDetector(null);
        universalDetector.handleData(fileBytes, 0, fileBytes.length);
        universalDetector.dataEnd();

        var encoding = universalDetector.getDetectedCharset();
        universalDetector.reset();

        if (encoding == null) {
            encoding = "UTF-8";
        }

        return encoding;
    }
}