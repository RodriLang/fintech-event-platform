package com.rodrilang.fintech.fraud.service;

import com.rodrilang.fintech.avro.PaymentEvent;

public interface FraudService {

     void evaluatePayment(PaymentEvent event);

}