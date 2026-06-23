package com.rodrilang.fintech.notification.consumer;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FraudNotificationConsumer {

    @KafkaListener(topics = "fraud-evaluated-events", groupId = "notification-service-group")
    public void listen(FraudEvaluatedEvent event) {
        log.info("[NOTIFICATION] Procesando aviso para el cliente: {}", event.getCustomerId());

        if ("APPROVED".equals(event.getStatus())) {
            log.info("ENVIANDO MAIL: 'Tu pago con ID {} fue aprobado con éxito. Gracias por usar nuestra Fintech!'",
                    event.getTransactionId());
        } else {
            log.warn("ENVIANDO ALERTA PUSH: 'ALERTA DE SEGURIDAD: Tu pago con ID {} fue RECHAZADO por: {}'",
                    event.getTransactionId(), event.getReason());
        }
    }
}