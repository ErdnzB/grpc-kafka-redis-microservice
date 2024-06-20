package com.demo.product.service;

import com.demo.product.mapper.ProductMapper;
import com.demo.product.model.ProductDto;
import com.demo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.demo.product.configuration.RedisConfiguration.CACHE_METHOD;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Cacheable(value = CACHE_METHOD, key = "#brand")
    public List<ProductDto> getProductsByBrand(String brand) {
        return productMapper.productsToProductDtoList(productRepository.findAllByBrand(brand));
    }
}
