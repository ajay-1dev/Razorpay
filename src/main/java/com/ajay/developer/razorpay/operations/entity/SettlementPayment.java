package com.ajay.developer.razorpay.operations.entity;

import com.ajay.developer.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "settlement_payment")
public class SettlementPayment extends BaseEntity {

    @EmbeddedId
    private SettlementPaymentId id;

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "settlement_id",nullable = false)
    private Settlement settlement;
}
