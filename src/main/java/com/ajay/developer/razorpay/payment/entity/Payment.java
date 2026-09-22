package com.ajay.developer.razorpay.payment.entity;

import com.ajay.developer.razorpay.common.entity.BaseEntity;
import com.ajay.developer.razorpay.common.entity.Money;
import com.ajay.developer.razorpay.common.enums.PaymentMethod;
import com.ajay.developer.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payment",indexes = {
        @Index(name = "idx_payment_order_id",columnList = "order_id"),
        @Index(name = "idx_payment_merchant_id",columnList = "merchant_id")
})
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderRecord order;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Column(nullable = false,length = 100)
    private String ideopotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private PaymentMethod method;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> methodDetails;

    @Column(length = 100)
    private String bankReference;

    @Column(length = 100)
    private String processorReference;

    @Column(length = 100)
    private String errorCode;

    @Column(length = 255)
    private String errorDetails;

    private LocalDateTime authorizedAt;

    private LocalDateTime capturedAt;

    private LocalDateTime failedAt;

    private LocalDateTime refundedAt;

    private LocalDateTime settledAt;






}
