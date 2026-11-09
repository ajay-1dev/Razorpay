package com.ajay.developer.razorpay.merchant.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@Slf4j
public class WebhookReceiverController {

    @PostMapping("/payment")
    public ResponseEntity<String> receiveWebhook(
            @RequestBody(required = false) String requestBody) {

        log.info("Webhook request received");
        log.info("Request body: {}", requestBody);

        return ResponseEntity.ok("SUCCESS");
    }
}