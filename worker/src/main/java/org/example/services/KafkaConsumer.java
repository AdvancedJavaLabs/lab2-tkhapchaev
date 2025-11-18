package org.example.services;

import org.example.models.Chunk;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final ChunkProcessor chunkProcessor;

    public KafkaConsumer(ChunkProcessor chunkProcessor) {
        this.chunkProcessor = chunkProcessor;
    }

    @KafkaListener(topics = "${app.kafka.consumer.topics.submitted-chunks.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "submittedChunksContainerFactory")
    public void listenSubmittedChunk(Chunk submittedChunk) {
        if (submittedChunk == null) {
            throw new IllegalArgumentException("Submitted chunk cannot be null");
        }

        chunkProcessor.process(submittedChunk);
    }
}