package com.rodrilang.fintech.payment.service;

import com.rodrilang.fintech.payment.dto.request.PaymentRequest;
import com.rodrilang.fintech.payment.dto.response.PaymentResponse;

public interface PaymentService {

     PaymentResponse initiatePayment(PaymentRequest request);

}