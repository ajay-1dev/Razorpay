package com.ajay.developer.razorpay.common.audit;

import com.ajay.developer.razorpay.merchant.security.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAwareImpl")
@RequiredArgsConstructor
public class AuditAwareImpl implements AuditorAware {

    private final MerchantContext merchantContext;


    @Override
    public Optional getCurrentAuditor() {
       try {
           if (merchantContext.getMerchantId() != null) {
               return Optional.of("MerchantId : " + merchantContext.getMerchantId());
           }

           if (merchantContext.getKeyId() != null && !merchantContext.getKeyId().isBlank()) {
               return Optional.of("KeyId : " + merchantContext.getKeyId());
           }
       } catch (Exception ignore) {

       }
        return Optional.of("SYSTEM");
    }
}
