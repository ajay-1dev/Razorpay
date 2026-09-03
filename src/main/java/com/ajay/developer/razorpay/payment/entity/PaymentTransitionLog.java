package com.ajay.developer.razorpay.payment.entity;

import com.ajay.developer.razorpay.common.enums.PaymentEvent;
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
@Table(name = "payment_transition_log")
public class PaymentTransitionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "payment_id",nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status",nullable = false,length = 30)
    private PaymentEvent fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "event",nullable = false,length = 30)
    private PaymentEvent Event;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status",nullable = false,length = 30)
    private PaymentEvent toStatus;

    @Column(name = "actor",length = 100)
    private String actor;

    @Column(name = "occured_at",nullable = false)
    private LocalDateTime occuredAt;


}
