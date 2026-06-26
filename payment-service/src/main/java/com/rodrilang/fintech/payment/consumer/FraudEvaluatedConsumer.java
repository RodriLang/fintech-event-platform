package com.rodrilang.fintech.payment.consumer;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import com.rodrilang.fintech.payment.model.PaymentStatus;
import com.rodrilang.fintech.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudEvaluatedConsumer {

    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "fraud-evaluated-events", groupId = "payment-service-group")
    @Transactional
    public void consume(FraudEvaluatedEvent event) {
        log.info("Resolucion de fraude recibida desde Kafka - ID: {}, Estado: {}",
                event.getTransactionId(), event.getStatus());

        paymentRepository.findById(event.getTransactionId())
                .ifPresentOrElse(payment -> {
                    PaymentStatus newStatus = PaymentStatus.valueOf(event.getStatus());
                    payment.setStatus(newStatus);

                    paymentRepository.save(payment);
                    log.info("Pago transaccionado con éxito. Estado final en DB: {}", newStatus);
                }, () -> log.error("Error crítico: No se encontró el pago original con ID: {}", event.getTransactionId()));
    }
}