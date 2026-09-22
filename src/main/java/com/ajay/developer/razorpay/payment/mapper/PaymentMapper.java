package com.ajay.developer.razorpay.payment.mapper;

import com.ajay.developer.razorpay.payment.dto.responce.PaymentResponce;
import com.ajay.developer.razorpay.payment.entity.Payment;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

//@Mapper(componentModel = "spring")
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(source = "order.id",target = "orderId")
    PaymentResponce toResponce(Payment payment);

    @Mapping(source = "order.id",target = "orderId")
   List<PaymentResponce> toResponceList(List<Payment> payments);

    @Mapping(source = "id", target = "paymentId")
    PaymentRequest toPaymentRequest(Payment payment);
}
