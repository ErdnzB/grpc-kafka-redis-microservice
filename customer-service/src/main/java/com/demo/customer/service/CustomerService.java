package com.demo.customer.service;

import com.demo.customer.client.OrderClient;
import com.demo.customer.client.ProductClient;
import com.demo.customer.entity.Customer;
import com.demo.customer.exception.CustomerRuntimeException;
import com.demo.customer.mapper.CustomerMapper;
import com.demo.customer.model.CreateCustomerRequest;
import com.demo.customer.model.CustomerDetailDto;
import com.demo.customer.model.CustomerDto;
import com.demo.customer.model.ProductDto;
import com.demo.customer.repository.CustomerRepository;
import com.demo.customer.service.producer.OrderEventProducer;
import com.demo.model.order.dto.OrderDto;
import com.demo.model.order.event.OrderEvent;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.Status;
import com.grpc.discount.DiscountExceptionResponse;
import com.grpc.discount.DiscountRequest;
import com.grpc.discount.DiscountResponse;
import com.grpc.discount.DiscountServiceGrpc;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final ProductClient productClient;
    private final OrderClient orderClient;
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final OrderEventProducer orderEventProducer;
    @GrpcClient("discount-calculator")
    private DiscountServiceGrpc.DiscountServiceBlockingStub discountBlockingStub;

    public CustomerDto createCustomer(CreateCustomerRequest request) {

        Customer customer = customerRepository.save(customerMapper.createCustomerRequestToCustomer(request));
        log.info("customer created");
        return customerMapper.customerToCustomerDto(customer);
    }

    public CustomerDetailDto getOrdersById(Long id) {

        log.info("get orders from customer");

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerRuntimeException("Can not find customer"));

        List<Long> productIdList = getProductIdList(id);

        List<ProductDto> products = productClient.getProductsByIds(productIdList);
        return CustomerDetailDto.builder()
                .age(customer.getAge())
                .name(customer.getName())
                .surname(customer.getSurname())
                .products(products)
                .build();
    }

    public void createOrder(Long customerId, Long productId) {
        log.info("CustomerId :{} and ProductId :{} for grpc request", customerId, productId);
        BigDecimal discountedPrice = getDiscount(productId);
        log.info("Grpc Response : {}", discountedPrice);
        OrderEvent orderEvent = OrderEvent.builder()
                .customerId(customerId)
                .productId(productId)
                .build();
        orderEventProducer.sendCreateOrder(orderEvent);
    }


    private BigDecimal getDiscount(Long productId) {
        DiscountRequest request = DiscountRequest.newBuilder()
                .setProductId(String.valueOf(productId))
                .setOriginalPrice("123.12")
                .build();
        try {
            DiscountResponse response = discountBlockingStub.calculateDiscount(request);
            return new BigDecimal(response.getDiscountedPrice());
        } catch (StatusRuntimeException e) {
            Status status = StatusProto.fromThrowable(e);
            for (Any any : status.getDetailsList()) {
                if (!any.is(DiscountExceptionResponse.class)) {
                    continue;
                }
                try {
                    DiscountExceptionResponse exceptionResponse = any.unpack(DiscountExceptionResponse.class);
                    System.out.println("timestamp: " + exceptionResponse.getTimestamp() +
                            ", errorCode : " + exceptionResponse.getErrorCode());
                } catch (InvalidProtocolBufferException ex) {
                    ex.printStackTrace();
                }
            }
            // System.out.println(status.getCode() + " : " + status.getDescription());
        }

        // return a default value
        return BigDecimal.ONE;
    }

    private List<Long> getProductIdList(Long id) {

        log.info("Called order service for orders of customer");

        return orderClient.listOrdersByCustomerId(id).stream()
                .map(OrderDto::getProductId)
                .toList();
    }

    public Long orderJobInfo() {
        return orderClient.getTotalOrderCount();
    }
}
