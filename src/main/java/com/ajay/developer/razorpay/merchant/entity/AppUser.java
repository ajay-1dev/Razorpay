package com.ajay.developer.razorpay.merchant.entity;

import com.ajay.developer.razorpay.common.entity.BaseEntity;
import com.ajay.developer.razorpay.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "app_user",indexes = {
        @Index(name = "idx_app_user_merchant_id",columnList = "merchant_id")
})
public class AppUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
