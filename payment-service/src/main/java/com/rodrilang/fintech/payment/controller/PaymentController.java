package com.rodrilang.fintech.payment.controller;

import com.rodrilang.fintech.payment.dto.request.PaymentRequest;
import com.rodrilang.fintech.payment.dto.response.PaymentResponse;
import com.rodrilang.fintech.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse processedPayment = paymentService.initiatePayment(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(processedPayment);
    }
}