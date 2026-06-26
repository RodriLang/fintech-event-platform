package com.rodrilang.fintech.notification.service.impl;

import com.rodrilang.fintech.avro.FraudEvaluatedEvent;
import com.rodrilang.fintech.notification.model.PaymentStatus;
import com.rodrilang.fintech.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void processAndSendAlert(FraudEvaluatedEvent event) {

        PaymentStatus currentStatus;
        try {
            currentStatus = PaymentStatus.valueOf(event.getStatus());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Estado desconocido o nulo recibido de Kafka: {}. Cancelando notificacion.", event.getStatus());
            return;
        }

        if (PaymentStatus.APPROVED.equals(currentStatus)) {
            sendEmail(event.getCustomerId(), event.getTransactionId());
        } else {
            String reason = event.getReason() != null ? event.getReason() : "Motivo desconocido";
            sendPushNotification(event.getCustomerId(), event.getTransactionId(), reason);
        }
    }

    // Se simula el envio de email que podria ser un cliente de SendGrid/AWS SES
    private void sendEmail(String customerId, String txId) {
        log.info("Preparando plantilla de éxito para Cliente: {}", customerId);
        log.info("Enviando correo: 'Tu pago con ID {} fue aprobado con éxito. ¡Gracias por elegirnos!'", txId);
    }

    //Se simula el envio de notificaciones que podria ser a traves de Firebase Cloud Messaging (FCM) o Apple APNS
    private void sendPushNotification(String customerId, String txId, String reason) {
        log.warn("Disparando alerta de seguridad al dispositivo móvil de: {}", customerId);
        log.warn("Notificación PUSH: 'Transacción {} RECHAZADA. Motivo: {}'", txId, reason);
    }
}