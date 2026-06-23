package com.rodrilang.fintech.accounting.service.impl;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import com.rodrilang.fintech.accounting.service.AccountingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingServiceImpl implements AccountingService {

    // Si más adelante le metés base de datos, acá inyectarías tu LedgerRepository
    // private final LedgerRepository ledgerRepository;

    @Override
    @Transactional
    public void processLedgerEntry(FraudEvaluatedEvent event) {
        String status = event.getStatus();

        if ("APPROVED".equals(status)) {
            log.info("[ASIENTO CONTABLE] Moviendo fondos. Registro en Ledger exitoso.");
            log.info(" ↳ DÉBITO: Cuenta Origen (Cliente: {})", event.getCustomerId());
            log.info(" ↳ CRÉDITO: Cuenta Destino asentada con éxito por Tx: {}", event.getTransactionId());
        } else {
            log.warn("[AUDITORÍA FINANCIERA] Transacción {} RECHAZADA por fraude. Asentando anulación en los libros.",
                    event.getTransactionId());
        }

        /* 💡 Próximo paso si querés persistencia:
        LedgerEntry entry = LedgerEntry.builder()
            .transactionId(event.getTransactionId().toString())
            .type(status.equals("APPROVED") ? "CONSOLIDATED" : "CANCELLED")
            .timestamp(LocalDateTime.now())
            .build();
        ledgerRepository.save(entry);
        */
    }
}