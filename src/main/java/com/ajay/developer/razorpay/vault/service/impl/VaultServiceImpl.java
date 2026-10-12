package com.ajay.developer.razorpay.vault.service.impl;

import com.ajay.developer.razorpay.common.entity.CardExpiryCheck;
import com.ajay.developer.razorpay.common.entity.Money;
import com.ajay.developer.razorpay.common.enums.CardBrand;
import com.ajay.developer.razorpay.common.exception.ResourceNotFoundException;
import com.ajay.developer.razorpay.common.util.RandomizerUtil;
import com.ajay.developer.razorpay.payment.processor.PaymentProcessor;
import com.ajay.developer.razorpay.payment.processor.PaymentProcessorRouter;
import com.ajay.developer.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.ajay.developer.razorpay.payment.processor.dto.responce.PaymentProcessorResponce;
import com.ajay.developer.razorpay.vault.config.VaultEncryptionConfig;
import com.ajay.developer.razorpay.vault.dto.request.TokenizeRequest;
import com.ajay.developer.razorpay.vault.dto.responce.TokenizeResponce;
import com.ajay.developer.razorpay.vault.entity.CardToken;
import com.ajay.developer.razorpay.vault.entity.VaultCard;
import com.ajay.developer.razorpay.vault.repository.CardTokenRepository;
import com.ajay.developer.razorpay.vault.repository.VaultCardRepository;
import com.ajay.developer.razorpay.vault.service.VaultService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {
    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;
    @Override
    @Transactional
    public TokenizeResponce tokenize(UUID merchantId, TokenizeRequest tokenizeRequest) {
        String lastFour = tokenizeRequest.pan().substring(tokenizeRequest.pan().length() - 4);
        String bin = tokenizeRequest.pan().substring(0,6);
        CardBrand cardBrand = this.detectBrand(tokenizeRequest.pan());
        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncryptor(dek)
                .encrypt(tokenizeRequest.pan().getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek = dekEncryptor.encrypt(dek);


        VaultCard vaultCard= VaultCard.builder()
                .brand(cardBrand.name())
                .expiryYear(tokenizeRequest.yearAndMonth().Year().toString())
                .expiryMonth(tokenizeRequest.yearAndMonth().Month().toString())
                .bin(bin)
                .lastFour(lastFour)
                .encryptedPan(encryptedPan)
                .encryptedDek(encryptedDek)
                .cardHolderName(tokenizeRequest.cardHolderName())
                .build();

        String token = "tok_"+ RandomizerUtil.randomBase64(32);

        CardToken cardToken = CardToken.builder()
                .token(token)
                .vaultCard(vaultCard)
                .customer(tokenizeRequest.customerId())
                .merchant(merchantId)
                .build();
        vaultCardRepository.save(vaultCard);
        cardTokenRepository.save(cardToken);

        return new TokenizeResponce(token,lastFour,cardBrand,tokenizeRequest.yearAndMonth().Month(),tokenizeRequest.yearAndMonth().Year());
    }

    @Override
    public CardBrand detectBrand(String pan) {
        if(pan.startsWith("4")) return CardBrand.VISA;
        if(pan.startsWith("5") || pan.startsWith("2")) return CardBrand.MASTERCARD;
        if(pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;
        return CardBrand.RUPAY;
    }

    @Override
    public PaymentProcessorResponce charge(UUID paymentId,String token, Money amount, Map<String, Object> methodDetails) {

        byte[] panBytes = null;
        try {
            CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token).orElseThrow(() -> new ResourceNotFoundException("token", token));
            VaultCard vaultCard = cardToken.getVaultCard();
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek)
                    .decrypt(vaultCard.getEncryptedPan());
            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(paymentId, pan, expiry, amount, methodDetails);
            PaymentProcessorResponce responce = paymentProcessorRouter.charge(paymentProcessorRequest);

            log.info("vault charge registered {}*************", token.substring(0, 4));
            Arrays.fill(panBytes, (byte) 0);


            return responce;
        }catch (Exception ex) {
            log.warn("vault charge failed{}*******",token);
            return new PaymentProcessorResponce.Failure("VAULT_CHARGE_FAILED", ex.getLocalizedMessage());
        } finally {
            if(panBytes != null)Arrays.fill(panBytes,(byte) 0);
        }
    }
}
