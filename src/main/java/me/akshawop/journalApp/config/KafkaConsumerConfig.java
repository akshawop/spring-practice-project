package me.akshawop.journalApp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

        public Map<String, Object> baseConsumerProps() {

                Map<String, Object> props = new HashMap<>();

                props.put(
                                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                "localhost:9092");

                props.put(
                                "spring.json.trusted.packages",
                                "me.akshawop.journalApp.*,java.lang");

                props.put(
                                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                                StringDeserializer.class);

                props.put(
                                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                                JsonDeserializer.class);

                // Read old messages if group is new
                props.put(
                                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                                "earliest");

                // Recommended for production
                props.put(
                                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                                false);

                // JsonDeserializer configs
                props.put(
                                JsonDeserializer.TRUSTED_PACKAGES,
                                "me.akshawop.journalApp.*,java.lang");

                props.put(
                                JsonDeserializer.USE_TYPE_INFO_HEADERS,
                                true);

                props.put(
                                JsonDeserializer.VALUE_DEFAULT_TYPE,
                                Object.class);

                return props;
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {

                ConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(
                                baseConsumerProps(),
                                new StringDeserializer(),
                                new JsonDeserializer<>());

                ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
                factory.setConsumerFactory(consumerFactory);
                // Manual ack
                factory.getContainerProperties().setAckMode(
                                ContainerProperties.AckMode.MANUAL);

                return factory;
        }
}