package me.akshawop.journalApp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

        public Map<String, Object> baseProducerProps() {

                Map<String, Object> props = new HashMap<>();

                props.put(
                                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                "localhost:9092");

                props.put(
                                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                StringSerializer.class);

                props.put(
                                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                JsonSerializer.class);

                props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);

                // Reliability
                props.put(
                                ProducerConfig.ACKS_CONFIG,
                                "all");

                props.put(
                                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                                true);

                // Performance
                props.put(
                                ProducerConfig.COMPRESSION_TYPE_CONFIG,
                                "snappy");

                props.put(
                                ProducerConfig.BATCH_SIZE_CONFIG,
                                32_768);

                props.put(
                                ProducerConfig.LINGER_MS_CONFIG,
                                5);

                // Retry
                props.put(
                                ProducerConfig.RETRIES_CONFIG,
                                Integer.MAX_VALUE);

                props.put(
                                ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
                                120000);

                return props;
        }

        @Bean
        public <T> ProducerFactory<String, T> producerFactory() {

                return new DefaultKafkaProducerFactory<String, T>(
                                baseProducerProps(),
                                new StringSerializer(),
                                new JsonSerializer<T>());
        }

        @Bean
        public <T> KafkaTemplate<String, T> kafkaTemplate() {

                return new KafkaTemplate<>(producerFactory());
        }
}