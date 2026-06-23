package com.rodrilang.fintech.fraud.consumer;

import com.rodrilang.fintech.avro.PaymentEvent;
import com.rodrilang.fintech.fraud.service.FraudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final FraudService fraudService;

    @KafkaListener(topics = "payment-events", groupId = "fraud-group")
    public void consumePaymentEvent(PaymentEvent event) {
        log.info("Evento recibido desde Kafka - ID Transación: {}, Cliente: {}, Monto: {}",
                event.getTransactionId(), event.getCustomerId(), event.getAmount());

        try {
            fraudService.evaluatePayment(event);
        } catch (Exception e) {
            log.error("Error procesando el evento de pago en el módulo de fraude", e);
            // NOTA: En producción aca se debe manejar una Dead Letter Topic (DLT) si el mensaje viene roto
        }
    }
}