package com.ajay.developer.razorpay.payment.simulator;

import com.ajay.developer.razorpay.common.enums.ChaosMode;
import com.ajay.developer.razorpay.common.enums.PaymentMethod;
import com.ajay.developer.razorpay.common.enums.PaymentStatus;
import com.ajay.developer.razorpay.common.util.RandomizerUtil;
import com.ajay.developer.razorpay.payment.entity.Payment;
import com.ajay.developer.razorpay.payment.repository.PaymentRepository;
import com.ajay.developer.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankCallBackSimulator {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final SimulatorConfig simulatorConfig;

    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallBack() {
        LocalDateTime globalTime = LocalDateTime.now().minusSeconds(1);
        List<Payment> candidates = paymentRepository
                .findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalTime);

        log.info("Number of candidates: {}", candidates.size());

        if(candidates.isEmpty()) {
            return;
        }
        for(Payment payment : candidates) {
            stimulateCallBack(payment);
        }
    }


    private void stimulateCallBack(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig = simulatorConfig.configFor(payment.getMethod());
        LocalDateTime dueAt = this.dueAt(payment,methodSimulatorConfig);
        if(LocalDateTime.now().isBefore(dueAt)) {
            return;
        }
        ChaosMode chaosMode = simulatorConfig.getChaosMode();
        switch (chaosMode){
            case ChaosMode.SUCCESS -> resolve(payment,true);
            case ChaosMode.FAIL -> resolve(payment,false);
            case ChaosMode.TIMEOUT -> {
                log.debug("bankCallBack simulator: Payment Timeout");
            }
            case ChaosMode.NORMAL , SlOW -> resolve(payment,ApproveFor(payment,methodSimulatorConfig));
        }
    }

    private boolean ApproveFor(Payment payment,SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodSimulatorConfig.getSucessRate();
    }

    private void resolve(Payment payment,boolean approve) {
        if(approve){
            String bankRef = "SIM_BANK_REF"+ RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorizing(payment.getId(),true,bankRef,null,null);
        }
        else{
            paymentService.resolveAuthorizing(payment.getId(),false,null,"SIM_BANK_ERROR_CODE","Simulated bank decline");
        }

    }
    private LocalDateTime dueAt(Payment payment,SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int range = methodSimulatorConfig.getMaxDelaySeconds() - methodSimulatorConfig.getMinDelaySeconds();
        int delaySeconds = methodSimulatorConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode() % (range+1));
        if(simulatorConfig.getChaosMode() == ChaosMode.SlOW){
            int minDelaySeconds = methodSimulatorConfig.getMinDelaySeconds()*2;
            delaySeconds *= 2;
        }
        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }

}
