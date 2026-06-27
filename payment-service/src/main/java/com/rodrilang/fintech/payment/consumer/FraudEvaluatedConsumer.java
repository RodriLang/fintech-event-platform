package com.rodrilang.fintech.payment.consumer;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import com.rodrilang.fintech.payment.model.Payment;
import com.rodrilang.fintech.payment.model.PaymentStatus;
import com.rodrilang.fintech.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        Optional<Payment> paymentOpt = paymentRepository.findById(event.getTransactionId());

        if (paymentOpt.isEmpty()) {
            log.warn("FANTASMA DETECTADO: Se recibió una resolución de fraude para una transacción que no existe en la DB: {}",
                    event.getTransactionId());
            return;
        }

        Payment payment = paymentOpt.get();

        try {
            payment.setStatus(PaymentStatus.valueOf(event.getStatus()));
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Estado desconocido o nulo recibido: {}. Fallo al mapear valor de enum.", event.getStatus());
            return;
        }

        paymentRepository.save(payment);

        log.info("Pago actualizado con éxito en la DB al estado: {}", event.getStatus());
    }
}