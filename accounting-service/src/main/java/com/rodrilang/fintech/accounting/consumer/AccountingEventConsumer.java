package com.rodrilang.fintech.accounting.consumer;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import com.rodrilang.fintech.accounting.service.AccountingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountingEventConsumer {

    private final AccountingService accountingService;

    @KafkaListener(topics = "fraud-evaluated-events", groupId = "accounting-service-group")
    public void consumeAccountingEvent(FraudEvaluatedEvent event) {
        log.info("Resolucion recibida para procesar en Ledger. Tx: {}, Estado: {}",
                event.getTransactionId(), event.getStatus());

        accountingService.processLedgerEntry(event);
    }
}