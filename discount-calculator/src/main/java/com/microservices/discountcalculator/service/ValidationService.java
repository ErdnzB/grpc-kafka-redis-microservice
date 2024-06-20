package com.microservices.discountcalculator.service;

import com.grpc.discount.DiscountErrorCode;
import com.microservices.discountcalculator.exception.DiscountCalculatorException;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public void validateProductCode(Integer productId) {
        checkIfNull(productId);
        checkIfValid(productId);
    }

    private void checkIfNull(Integer productId) {
        if (productId == null) {
            throw new DiscountCalculatorException(DiscountErrorCode.INVALID_PRODUCT_ID);
        }
    }

    private void checkIfValid(Integer productId) {
        if (productId < 1 || productId > 81) {
            throw new DiscountCalculatorException(DiscountErrorCode.INVALID_PRODUCT_ID);
        }
    }
}
