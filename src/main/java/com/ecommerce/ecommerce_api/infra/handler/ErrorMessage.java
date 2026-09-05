package com.ecommerce.ecommerce_api.infra.handler;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class ErrorMessage {

    private String message;
    private int status;
    private String error;
    private String developer_message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
