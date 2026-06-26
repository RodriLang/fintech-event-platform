package com.rodrilang.fintech.fraud.producer;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudProducer {

    private final KafkaTemplate<String, FraudEvaluatedEvent> kafkaTemplate;
    private static final String TOPIC = "fraud-evaluated-events";

    public void sendFraudResult(FraudEvaluatedEvent event) {
        log.info("Publicando resolución de fraude en Kafka. ID Transacción: {}, Resultado: {}",
                event.getTransactionId(), event.getStatus());

        kafkaTemplate.send(TOPIC, event.getTransactionId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error al enviar la resolucion a Kafka", ex);
                    } else {
                        log.info("Resolucion enviado con éxito. Partition: {}, Offset: {}",
                                result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }
}