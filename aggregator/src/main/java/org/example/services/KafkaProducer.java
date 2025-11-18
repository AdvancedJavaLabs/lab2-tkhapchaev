package org.example.services;

import org.example.models.AggregatedResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.producer.topics.aggregated-results.name}")
    private String aggregatedResultsTopicName;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAggregatedResult(AggregatedResult aggregatedResult) {
        if (aggregatedResult == null) {
            throw new IllegalArgumentException("Aggregated result cannot be null");
        }

        kafkaTemplate.send(aggregatedResultsTopicName, aggregatedResult.getSubmissionInfo().getTextId().toString(), aggregatedResult);
    }
}