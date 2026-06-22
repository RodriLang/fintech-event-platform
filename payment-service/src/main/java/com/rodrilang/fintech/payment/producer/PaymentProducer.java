package com.rodrilang.fintech.payment.producer;

import com.rodrilang.fintech.avro.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private static final String TOPIC = "payment-events";

    public void sendPaymentEvent(PaymentEvent event) {
        log.info("Publicando evento de pago en Kafka. ID: {}, Cliente: {}", event.getTransactionId(), event.getCustomerId());

        CompletableFuture<SendResult<String, PaymentEvent>> future =
                kafkaTemplate.send(TOPIC, event.getCustomerId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento enviado con éxito a Kafka. Partition: {}, Offset: {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Error crítico al enviar evento a Kafka para la transacción: {}",
                        event.getTransactionId(), ex);
                // NOTA: En producción acá se podría implementar una lógica de reintento en DB
            }
        });
    }
}