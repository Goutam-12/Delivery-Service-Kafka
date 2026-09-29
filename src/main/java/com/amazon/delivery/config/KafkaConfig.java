package com.amazon.delivery.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.amazon.delivery.event.PaymentSuccessEvent;

@Configuration
public class KafkaConfig {

    // PRODUCER CONFIGURATION
    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class);

        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }


    // KAFKA TEMPLATE
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {

        return new KafkaTemplate<>(producerFactory());
    }


    // CONSUMER CONFIGURATION
    @Bean
    public ConsumerFactory<String, PaymentSuccessEvent> consumerFactory() {

        Map<String, Object> config = new HashMap<>();

        // Kafka broker
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        // Consumer group
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"delivery-service-group");

        // Start from earliest available message
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        // Spring Kafka manages commits
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);


        // ERROR HANDLING DESERIALIZER
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,ErrorHandlingDeserializer.class);

        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,ErrorHandlingDeserializer.class);

        // Actual key deserializer
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,StringDeserializer.class);

        // Actual value deserializer
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,JsonDeserializer.class);


        // JSON DESERIALIZER CONFIGURATION

        /*
         * The producer is sending:
         *
         * com.amazon.payment.event.PaymentSuccessEvent
         *
         * Therefore this package must be trusted.
         *
         * We also trust the delivery package because the local
         * PaymentSuccessEvent class belongs to this service.
         */
        
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.amazon.payment.event,com.amazon.delivery.event");


        /*
         * If Kafka does not provide type information, deserialize
         * the JSON into the delivery service's PaymentSuccessEvent.
         */
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE,PaymentSuccessEvent.class);

        /*
         * Do not require the producer's Java class to exist in
         * this service. Instead, use the configured target type.
         */
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,false);

        return new DefaultKafkaConsumerFactory<>(config);
    }


    // LISTENER CONTAINER FACTORY
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessEvent>
            kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessEvent>
                factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
