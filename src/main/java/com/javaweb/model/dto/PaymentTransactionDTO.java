package com.javaweb.model.dto;

import java.time.LocalDateTime;

public interface PaymentTransactionDTO {
    Long getTransactionID();
    String getTransactionCode();
    String getPaymentMethod();
    Double getAmount();
    String getCurrency();
    String getPaymentStatus();
    LocalDateTime getPaymentDate();
    String getCourseTitle();
}
