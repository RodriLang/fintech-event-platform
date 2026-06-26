package com.rodrilang.fintech.notification.service;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;

public interface NotificationService {
    void processAndSendAlert(FraudEvaluatedEvent event);
}