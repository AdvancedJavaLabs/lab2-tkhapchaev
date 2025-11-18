package org.example.services;

import org.example.models.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.producer.topics.submitted-chunks.name}")
    private String submittedChunksTopicName;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishSubmittedChunks(List<Chunk> submittedChunks) {
        if (submittedChunks == null) {
            throw new IllegalArgumentException("Submitted chunks cannot be null");
        }

        for (var chunk : submittedChunks) {
            kafkaTemplate.send(submittedChunksTopicName, chunk.getSubmissionInfo().getTextId().toString(), chunk);
        }
    }
}