package com.ajay.developer.razorpay.vault.customvalidations;

import com.ajay.developer.razorpay.common.entity.CardExpiryCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ExpiryCheckValidator implements ConstraintValidator<ExpiryCheck, CardExpiryCheck> {
    @Override
    public boolean isValid(CardExpiryCheck value, ConstraintValidatorContext context) {

        if (value == null || value.Month() == null || value.Year() == null) {
            return false;
        }
        LocalDate current = LocalDate.now();
        Integer expiryMonth = value.Month();;
        Integer expiryYear = value.Year();

        if(expiryMonth < 1 || expiryMonth > 12){
            return false;
        }
        if(expiryYear > current.getYear()){
            return true;
        }
        else if(expiryYear == current.getYear() && expiryMonth >= current.getMonthValue()){
            return true;
        }
        return false;
    }
}
