package com.rodrilang.fintech.payment.service.impl;

import com.rodrilang.fintech.avro.PaymentEvent;
import com.rodrilang.fintech.payment.dto.request.PaymentRequest;
import com.rodrilang.fintech.payment.dto.response.PaymentResponse;
import com.rodrilang.fintech.payment.mapper.PaymentMapper;
import com.rodrilang.fintech.payment.model.Payment;
import com.rodrilang.fintech.payment.model.PaymentStatus;
import com.rodrilang.fintech.payment.producer.PaymentProducer;
import com.rodrilang.fintech.payment.repository.PaymentRepository;
import com.rodrilang.fintech.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        String transactionId = UUID.randomUUID().toString();

        Payment payment = paymentMapper.toEntity(request);
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);
        PaymentEvent paymentEvent = mapToEvent(payment);

        sendEventPostCommit(paymentEvent);

        return paymentMapper.toResponse(savedPayment);
    }

    private PaymentEvent mapToEvent(Payment payment) {

        return PaymentEvent.newBuilder()
                .setTransactionId(payment.getTransactionId())
                .setCustomerId(payment.getCustomerId())
                .setAmount(payment.getAmount())
                .setCurrency(payment.getCurrency())
                .setStatus(payment.getStatus().name())
                .build();
    }

    private void sendEventPostCommit(PaymentEvent event){

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                paymentProducer.sendPaymentEvent(event);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String transactionId) {
        return paymentRepository.findById(transactionId)
                .map(payment ->  new PaymentResponse(
                        payment.getTransactionId(),
                        payment.getCustomerId(),
                        payment.getDestinationAccountId(),
                        payment.getAmount(),
                        payment.getCurrency(),
                        payment.getStatus()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pago no encontrado con ID: " + transactionId
                ));
    }
}