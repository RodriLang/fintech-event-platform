package com.rodrilang.fintech.accounting.service.impl;

import com.rodrilang.fintech.accounting.model.LedgerEntry;
import com.rodrilang.fintech.accounting.model.PaymentStatus;
import com.rodrilang.fintech.accounting.repository.LedgerRepository;
import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import com.rodrilang.fintech.accounting.service.AccountingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingServiceImpl implements AccountingService {

    private final LedgerRepository ledgerRepository;

    @Override
    @Transactional
    public void processLedgerEntry(FraudEvaluatedEvent event) {
        if (event == null || event.getTransactionId() == null) {
            log.error("[CONTABILIDAD] Evento recibido nulo o sin ID de transacción. Ignorando registro.");
            return;
        }

        String transactionId = event.getTransactionId();

        if (ledgerRepository.existsById(transactionId)) {
            log.warn("[CONTABILIDAD] Mensaje duplicado detectado para Tx: {}. Ignorando asiento contable.", transactionId);
            return;
        }

        PaymentStatus currentStatus;
        try {
            currentStatus = PaymentStatus.valueOf(event.getStatus());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("[CONTABILIDAD] Estado desconocido o nulo recibido: {}. Fallo al mapear enum.", event.getStatus());
            return;
        }

        if (PaymentStatus.APPROVED.equals(currentStatus)) {
            log.info("[ASIENTO CONTABLE] Moviendo fondos. Registrando asiento para el Cliente: {}", event.getCustomerId());
        } else {
            log.warn("[AUDITORÍA FINANCIERA] Asentando registro de anulación para Tx Rechazada: {}", transactionId);
        }

        LedgerEntry entry = LedgerEntry.builder()
                .transactionId(transactionId)
                .customerId(event.getCustomerId())
                .status(currentStatus)
                .createdAt(LocalDateTime.now())
                .build();

        ledgerRepository.save(entry);
        log.info("[LEDGER CONSOLIDADO] Transacción {} guardada con éxito en accounting_db.", transactionId);
    }
}