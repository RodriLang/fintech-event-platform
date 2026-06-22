package com.rodrilang.fintech.payment.dto.response;

import com.rodrilang.fintech.payment.model.PaymentStatus;

public record PaymentResponse (

     String transactionId,

     String customerId,

     String destinationAccountId,

     Double amount,

     String currency,

     PaymentStatus status
){
}