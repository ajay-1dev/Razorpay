package com.ajay.developer.razorpay.merchant.entity;

import com.ajay.developer.razorpay.common.entity.BaseEntity;
import com.ajay.developer.razorpay.common.enums.Environment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "api_key", indexes = {
       // @Index(name = "idx_api_key_merchant_id",columnList = "merchant_id"),
        @Index(name = "idx_api_key_merchant_env",columnList = "merchant_id , environment , enabled")
})
//@SQLDelete(sql = "UPDATE api_key SET enabled = false WHERE Id = ?")
//@SQLRestriction("enabled = true")
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false,length = 200,unique = true)
    private String keyId;

    @Column(nullable = false,length = 200)
    private String keySecretHash;

    @Column(length = 200)
    private String previousKeySecretHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 10)
    private Environment environment;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    private LocalDateTime lastUsedAt;

    private LocalDateTime createdAt;

    private LocalDateTime rotatedAt;

    private LocalDateTime gracePeriodExpiredAt;
}
