package com.rodrilang.fintech.fraud.service.impl;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import com.rodrilang.fintech.avro.PaymentEvent;
import com.rodrilang.fintech.fraud.model.FraudCheck;
import com.rodrilang.fintech.fraud.model.FraudCheckStatus;
import com.rodrilang.fintech.fraud.producer.FraudProducer;
import com.rodrilang.fintech.fraud.repository.FraudCheckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import com.rodrilang.fintech.fraud.service.FraudService;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudServiceImpl implements FraudService {

    private final FraudCheckRepository fraudCheckRepository;
    private final FraudProducer fraudProducer;
    private static final Double MAX_ALLOWED_AMOUNT = 10000.00;

    @Override
    @Transactional
    public void evaluatePayment(PaymentEvent event) {
        log.info("Analizando riesgo para la transacción: {}", event.getTransactionId());

        FraudCheckStatus status = FraudCheckStatus.APPROVED;
        String reason = "Monto dentro de los límites seguros.";

        if (event.getAmount() > MAX_ALLOWED_AMOUNT) {
            status = FraudCheckStatus.REJECTED_HIGH_AMOUNT;
            reason = "El monto supera el límite permitido de " + MAX_ALLOWED_AMOUNT;
            log.warn("ALERTA DE FRAUDE. Transacción {} rechazada. Monto: {}", event.getTransactionId(), event.getAmount());
        }

        FraudCheck check = FraudCheck.builder()
                .transactionId(event.getTransactionId())
                .customerId(event.getCustomerId())
                .amount(event.getAmount())
                .result(status)
                .reason(reason)
                .checkedAt(LocalDateTime.now())
                .build();

        fraudCheckRepository.save(check);
        log.info("Evaluación de fraude guardada en DB con resultado: {}", status);

        FraudEvaluatedEvent evaluatedEvent =
                com.rodrilang.fintech.avro.FraudEvaluatedEvent.newBuilder()
                        .setTransactionId(event.getTransactionId())
                        .setCustomerId(event.getCustomerId())
                        .setStatus(status.toString())
                        .setReason(reason)
                        .build();

        fraudProducer.sendFraudResult(evaluatedEvent);
        // TODO: En el próximo paso, acá deberíamos disparar un evento "FraudEvaluatedEvent"
        // hacia Kafka para avisarle a payment-service si debe confirmar o cancelar el pago.
    }
}