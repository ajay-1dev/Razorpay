package com.ajay.developer.razorpay.vault.customvalidations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {ExpiryCheckValidator.class })
@Target({FIELD})
@Retention(RUNTIME)
public @interface ExpiryCheck{

    String message() default "{Expiry month is in valid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };


}
