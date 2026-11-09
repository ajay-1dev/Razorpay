package com.ajay.developer.razorpay.operations.settlement.normal;

import com.ajay.developer.razorpay.common.entity.Money;
import com.ajay.developer.razorpay.operations.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount,
                                String bankAccount, String ifsc);
}
