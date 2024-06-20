package com.demo.product.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter

public class ProductRuntimeException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	private final int code;
	private final HttpStatus status;

	public ProductRuntimeException(int code, String message) {
		this(BAD_REQUEST, code, message);
	}

	public ProductRuntimeException(HttpStatus status, int code, String message) {
		super(message);
		this.code = code;
		this.status = status;
	}
}