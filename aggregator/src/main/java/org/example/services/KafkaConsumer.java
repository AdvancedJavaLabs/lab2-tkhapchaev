package org.example.services;

import org.example.models.Chunk;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final ChunkAggregator chunkAggregator;

    public KafkaConsumer(ChunkAggregator chunkAggregator) {
        this.chunkAggregator = chunkAggregator;
    }

    @KafkaListener(topics = "${app.kafka.consumer.topics.processed-chunks.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "processedChunksContainerFactory")
    public void listenProcessedChunk(Chunk processedChunk) {
        if (processedChunk == null) {
            throw new IllegalArgumentException("Processed chunk cannot be null");
        }

        chunkAggregator.addProcessedChunkAndTryPublish(processedChunk);
    }
}