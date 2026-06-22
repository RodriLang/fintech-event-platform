package com.rodrilang.fintech.payment.mapper;

import com.rodrilang.fintech.payment.dto.request.PaymentRequest;
import com.rodrilang.fintech.payment.dto.response.PaymentResponse;
import com.rodrilang.fintech.payment.model.Payment;

public interface PaymentMapper {

    PaymentResponse toResponse (Payment entity);

    Payment toEntity (PaymentRequest dto);

}
