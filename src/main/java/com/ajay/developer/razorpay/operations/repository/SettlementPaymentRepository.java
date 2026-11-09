package com.ajay.developer.razorpay.operations.repository;

import com.ajay.developer.razorpay.operations.entity.SettlementPayment;
import com.ajay.developer.razorpay.operations.entity.SettlementPaymentId;
import com.ajay.developer.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ajay.developer.razorpay.operations.entity.SettlementPayment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT p FROM Payment p
    WHERE p.id IN (
        SELECT sp.id.paymentId
        FROM SettlementPayment sp
        WHERE sp.id.settlementId = :settlementId
    )
""")
    List<Payment> findBySettlementIdForUpdate(
            @Param("settlementId") UUID settlementId
    );
}