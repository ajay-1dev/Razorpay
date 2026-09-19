package com.ajay.developer.razorpay.payment.entity;

import com.ajay.developer.razorpay.common.entity.BaseEntity;
import com.ajay.developer.razorpay.common.entity.Money;
import com.ajay.developer.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_record", indexes = {
        @Index(name = "idx_order_id_merchant_id",columnList = "id , merchant_id"),
        @Index(name = "idx_order_merchant_id",columnList = "merchant_id")
})
public class OrderRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //no foriegn key CROSS SERVICE BOUNDARY
    @Column(name = "merchant_id",nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Column(length = 100)
    private String receipt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> notes;

    @Column(name = "expires_at",nullable = false)
    private LocalDateTime expiresAt;
}
