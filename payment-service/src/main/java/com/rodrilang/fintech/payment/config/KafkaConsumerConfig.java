package com.rodrilang.fintech.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    private static final long INTERVAL_ATTEMPTS = 2000L;
    private static final long MAX_ATTEMPTS = 3L;

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        FixedBackOff backOff = new FixedBackOff(INTERVAL_ATTEMPTS, MAX_ATTEMPTS);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        errorHandler.setRetryListeners((consumerRecord, ex, deliveryAttempt)
                -> log.warn("Reintento fallido número {} para el mensaje en la partición {}. Error: {}",
                deliveryAttempt, consumerRecord.partition(), ex.getMessage()));

        return errorHandler;
    }
}