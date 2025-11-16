package org.example.services;

import org.example.models.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.producer.topics.processed-chunks.name}")
    private String processedChunksTopicName;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishProcessedChunk(Chunk processedChunk) {
        if (processedChunk == null) {
            throw new IllegalArgumentException("Processed chunks cannot be null");
        }

        kafkaTemplate.send(processedChunksTopicName, processedChunk.getSubmissionInfo().getTextId().toString(), processedChunk);
    }
}