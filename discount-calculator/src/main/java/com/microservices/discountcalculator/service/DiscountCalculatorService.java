package com.microservices.discountcalculator.service;

import com.grpc.discount.DiscountRequest;
import com.grpc.discount.DiscountResponse;
import com.grpc.discount.DiscountServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@GrpcService
public class DiscountCalculatorService extends DiscountServiceGrpc.DiscountServiceImplBase {

    @Autowired
    private ValidationService validationService;

    @Override
    public void calculateDiscount(DiscountRequest request, StreamObserver<DiscountResponse> responseObserver) {
        // System.out.println("Request received from client:\n" + request);
        validationService.validateProductCode(Integer.valueOf(request.getProductId()));
        BigDecimal price = new BigDecimal(request.getOriginalPrice());
        BigDecimal discountedPrice = price.divide(BigDecimal.valueOf(10));

        DiscountResponse response = DiscountResponse.newBuilder()
                .setDiscountedPrice(discountedPrice.toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
