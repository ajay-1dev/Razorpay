package com.ajay.developer.razorpay.merchant.entity;

import com.ajay.developer.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "merchant_webhook_config",indexes = {
        @Index(name = "idx_merchant_webhook_config_merchant_id",columnList = "merchant_id , enabled")
})
public class MerchantWebhookConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private  Merchant merchant;

    @Column(nullable = false,length = 500)
    private String targetUrl;

    @Column(length = 255)
    private String webhookSecretHash;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(length = 255)
    private String eventTypes;


}
