package com.ecommerce.ecommerce_api.exception;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {
    private String message;
    private String title;
    private int status;
    private String error;
    private String details;
    private String developer_message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
