package com.rodrilang.fintech.payment.dto.response;

import com.rodrilang.fintech.payment.model.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse (

     String transactionId,

     String customerId,

     String destinationAccountId,

     BigDecimal amount,

     String currency,

     PaymentStatus status
){
}