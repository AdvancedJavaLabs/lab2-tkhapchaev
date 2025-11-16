package org.example.configurations;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.models.AggregatedResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AggregatedResult> aggregatedResultsContainerFactory(ConsumerFactory<Object, Object> baseFactory) {
        JsonDeserializer<AggregatedResult> jsonDeserializer = new JsonDeserializer<>(AggregatedResult.class);
        jsonDeserializer.addTrustedPackages("*");

        var configurationProperties = new HashMap<>(baseFactory.getConfigurationProperties());
        configurationProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        var consumerFactory = new DefaultKafkaConsumerFactory<>(configurationProperties, new StringDeserializer(), jsonDeserializer);
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, AggregatedResult>();
        containerFactory.setConsumerFactory(consumerFactory);

        return containerFactory;
    }
}