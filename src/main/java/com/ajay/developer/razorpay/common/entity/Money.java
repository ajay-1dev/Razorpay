package com.ajay.developer.razorpay.common.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class Money {
    private int amountUnits;
    private String currency;

    private Money(int amountUnits, String currency) {
        this.amountUnits = amountUnits;
        this.currency = currency;
    }

    public static Money of(int amountUnits, String currency) {
        return new Money(amountUnits, currency);
    }

    public static Money inr(int amountUnits) {
        return new Money(amountUnits, "INR");
    }

    public Money add(Money other){
        if(!this.currency.equals(other.currency)){
            throw new IllegalArgumentException("cannot add money with different currency");
        }
        return new Money(this.amountUnits + other.amountUnits, this.currency);
    }
    public Money subtract(Money other){
        if(!this.currency.equals(other.currency)){
            throw new IllegalArgumentException("cannot subtract money with different currency");
        }
        return new Money(this.amountUnits - other.amountUnits, this.currency);
    }

}
