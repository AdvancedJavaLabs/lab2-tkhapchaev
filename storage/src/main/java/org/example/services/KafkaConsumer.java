package org.example.services;

import org.example.models.AggregatedResult;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaConsumer {
    private final StorageService storageService;

    public KafkaConsumer(StorageService storageService) {
        this.storageService = storageService;
    }

    @KafkaListener(topics = "${app.kafka.consumer.topics.aggregated-results.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "aggregatedResultsContainerFactory")
    public void listenAggregatedResult(AggregatedResult aggregatedResult) throws IOException {
        if (aggregatedResult == null) {
            throw new IllegalArgumentException("Aggregated result cannot be null");
        }

        storageService.save(aggregatedResult);
    }
}