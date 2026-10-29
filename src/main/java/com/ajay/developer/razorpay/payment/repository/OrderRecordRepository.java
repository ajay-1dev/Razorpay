package com.ajay.developer.razorpay.payment.repository;

import com.ajay.developer.razorpay.payment.entity.OrderRecord;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRecordRepository extends JpaRepository<OrderRecord, UUID> {

 boolean existsByMerchantIdAndReceipt(UUID merchantId,String receipt);

    Optional<OrderRecord> findByIdAndMerchantId(UUID orderId, UUID merchantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderRecord o WHERE o.id = :uuid AND o.merchantId = :merchantId")
    Optional<OrderRecord> findByIdAndMerchantIdForUpdate(@Param("uuid") UUID uuid, @Param("merchantId") UUID merchantId);
}