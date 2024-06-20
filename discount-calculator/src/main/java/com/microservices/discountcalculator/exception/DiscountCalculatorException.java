package com.microservices.discountcalculator.exception;

import com.grpc.discount.DiscountErrorCode;
import lombok.Getter;

@Getter
public class DiscountCalculatorException extends RuntimeException {

    // TODO: NilS
    private static final long serialVersionUID = -8111656859346000121L;

    private final DiscountErrorCode errorCode;

    public DiscountCalculatorException(DiscountErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}