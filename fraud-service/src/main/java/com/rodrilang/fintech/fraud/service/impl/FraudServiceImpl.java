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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.rodrilang.fintech.fraud.service.FraudService;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudServiceImpl implements FraudService {

    private final FraudCheckRepository fraudCheckRepository;
    private final FraudProducer fraudProducer;

    private static final BigDecimal SUSPECT_THRESHOLD = new BigDecimal("50000.00");
    private static final BigDecimal MAX_ALLOWED_AMOUNT = new BigDecimal("100000.00");

    @Override
    @Transactional
    public void evaluatePayment(PaymentEvent event) {
        log.info("Analizando riesgo para la transacción: {}", event.getTransactionId());

        BigDecimal amount = event.getAmount();
        FraudCheckStatus status;
        String reason;

        if (amount.compareTo(MAX_ALLOWED_AMOUNT) > 0) {
            status = FraudCheckStatus.REJECTED_HIGH_AMOUNT;
            reason = "El monto supera el límite permitido de " + MAX_ALLOWED_AMOUNT;
            log.warn("ALERTA DE FRAUDE. Transacción {} rechazada. Monto: {}", event.getTransactionId(), event.getAmount());

        } else if (amount.compareTo(SUSPECT_THRESHOLD) > 0) {
            status = FraudCheckStatus.PENDING_REVIEW;
            reason = "Monto elevado en zona gris. Requiere monitoreo preventivo.";
            log.warn("TRANSACCION SOSPECHOSA. Transaccion {} bajo la lupa. Monto: {}", event.getTransactionId(), event.getAmount());
        } else {
            status = FraudCheckStatus.APPROVED;
            reason = "Monto dentro de los límites seguros.";
        }

        FraudCheck check = FraudCheck.builder()
                .transactionId(event.getTransactionId())
                .customerId(event.getCustomerId())
                .amount(amount)
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
    }
}