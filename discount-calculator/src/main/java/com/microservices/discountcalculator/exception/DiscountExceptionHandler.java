package com.microservices.discountcalculator.exception;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.google.rpc.Code;
import com.google.rpc.Status;
import com.grpc.discount.DiscountExceptionResponse;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.time.Instant;

@GrpcAdvice
public class DiscountExceptionHandler {

    @GrpcExceptionHandler(DiscountCalculatorException.class)
    public StatusRuntimeException handleValidationError(DiscountCalculatorException cause) {

        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();

        DiscountExceptionResponse exceptionResponse =
                DiscountExceptionResponse.newBuilder()
                        .setErrorCode(cause.getErrorCode())
                        .setTimestamp(timestamp)
                        .build();


        Status status = Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT.getNumber())
                .setMessage("Invalid discount code")
                .addDetails(Any.pack(exceptionResponse))
                .build();

        return StatusProto.toStatusRuntimeException(status);
    }
}
