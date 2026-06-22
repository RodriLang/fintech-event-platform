package com.rodrilang.fintech.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(

        @NotBlank(message = "El customerId no puede estar vacío")
        String customerId,

        @NotBlank(message = "El destinationAccountId no puede estar vacío")

        String destinationAccountId,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        Double amount,

        @NotBlank(message = "La moneda (currency) no puede estar vacía")
        String currency
) {
}