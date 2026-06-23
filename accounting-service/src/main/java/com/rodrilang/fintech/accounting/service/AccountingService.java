package com.rodrilang.fintech.accounting.service;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;

public interface AccountingService {
    void processLedgerEntry(FraudEvaluatedEvent event);
}