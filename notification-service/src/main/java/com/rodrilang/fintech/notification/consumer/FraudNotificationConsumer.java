package com.rodrilang.fintech.notification.consumer;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import com.rodrilang.fintech.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudNotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "fraud-evaluated-events", groupId = "notification-service-group")
    public void listen(FraudEvaluatedEvent event) {
        if (event == null || event.getTransactionId() == null) {
            log.error("Evento recibido nulo o inválido. Cancelando despacho.");
            return;
        }

        log.info("Evento capturado con éxito de Kafka para Tx: {}", event.getTransactionId());

        notificationService.processAndSendAlert(event);
    }
}