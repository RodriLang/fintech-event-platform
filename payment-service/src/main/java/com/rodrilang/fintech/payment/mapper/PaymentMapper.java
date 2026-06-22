package com.rodrilang.fintech.payment.mapper;

import com.rodrilang.fintech.payment.dto.request.PaymentRequest;
import com.rodrilang.fintech.payment.dto.response.PaymentResponse;
import com.rodrilang.fintech.payment.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponse toResponse (Payment entity);

    @Mapping(target = "transactionId", ignore = true)
    Payment toEntity (PaymentRequest dto);

}
