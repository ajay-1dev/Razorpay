package com.ajay.developer.razorpay.payment.repository;

import com.ajay.developer.razorpay.common.enums.OutboxStatus;
import com.ajay.developer.razorpay.payment.entity.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {



    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}