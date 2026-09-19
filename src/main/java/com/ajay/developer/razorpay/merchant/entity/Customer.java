package com.ajay.developer.razorpay.merchant.entity;

import com.ajay.developer.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "customer",indexes = {
        @Index(name = "idx_customer_merchant_id",columnList = "merchant_id"),
        @Index(name = "idx_customer_email",columnList = "email")
})
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Column(length = 200)
    private String name;

    @Column(length = 200)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    private LocalDateTime deletedAt;
}
