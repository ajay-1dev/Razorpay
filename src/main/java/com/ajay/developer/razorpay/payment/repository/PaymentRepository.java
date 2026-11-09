package com.ajay.developer.razorpay.payment.repository;

import com.ajay.developer.razorpay.common.enums.PaymentStatus;
import com.ajay.developer.razorpay.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder_Id(UUID orderId);

    Optional<Payment> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime globalTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :paymentId AND p.merchantId = :merchantId")
    Optional<Payment> findByIdAndMerchantIdForUpdate(@Param("paymentId") UUID paymentId, @Param("merchantId") UUID merchantId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :paymentId")
    Optional<Payment> findByIdForUpdate(@Param("paymentId") UUID paymentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.merchantId = :merchantId and p.status = :paymentStatus and p.settledAt is null")
    List<Payment> findByMerchantIdAndStatusForUpdate(UUID merchantId, PaymentStatus paymentStatus);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT p FROM Payment p WHERE p.id IN (
        SELECT sp.id.paymentId
        FROM SettlementPayment sp
        WHERE sp.id.settlementId = :settlementId
    )
""")
    List<Payment> findBySettlementIdForUpdate(@Param("settlementId") UUID settlementId);
}