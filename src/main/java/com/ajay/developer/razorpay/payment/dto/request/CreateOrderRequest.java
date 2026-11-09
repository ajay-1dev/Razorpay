package com.ajay.developer.razorpay.payment.dto.request;

import com.ajay.developer.razorpay.common.entity.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(
        @NotNull(message = "Amount is required")
        @Embedded
       Money amount,

       @Size(max = 100)
       String receipt,


       @JdbcTypeCode((SqlTypes.JSON))
       @Column(columnDefinition = "jsonb")
       Map<String,Object> notes,

       LocalDateTime expiresAt,

        @Valid
        CustomerDetails customer
) {

    public record CustomerDetails(
            @Size(max = 200)
            String name,

            @Email
            @Size(max = 200)
            String email,

            @Size(max = 20)
            String phone
    ){}
}
