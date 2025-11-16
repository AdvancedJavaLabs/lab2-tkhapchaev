package org.example.configurations;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.models.Chunk;
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
    public ConcurrentKafkaListenerContainerFactory<String, Chunk> processedChunksContainerFactory(ConsumerFactory<Object, Object> baseFactory) {
        JsonDeserializer<Chunk> jsonDeserializer = new JsonDeserializer<>(Chunk.class);
        jsonDeserializer.addTrustedPackages("*");

        var configurationProperties = new HashMap<>(baseFactory.getConfigurationProperties());
        configurationProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        var consumerFactory = new DefaultKafkaConsumerFactory<>(configurationProperties, new StringDeserializer(), jsonDeserializer);
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, Chunk>();
        containerFactory.setConsumerFactory(consumerFactory);

        return containerFactory;
    }
}