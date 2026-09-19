package com.ajay.developer.razorpay.payment.mapper;

import com.ajay.developer.razorpay.payment.dto.responce.OrderResponce;
import com.ajay.developer.razorpay.payment.entity.OrderRecord;
import jakarta.persistence.criteria.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(source = "amount", target = "money")
    @Mapping(source = "orderStatus", target = "status")
    OrderResponce orderToOrderResponce(OrderRecord order);
}
