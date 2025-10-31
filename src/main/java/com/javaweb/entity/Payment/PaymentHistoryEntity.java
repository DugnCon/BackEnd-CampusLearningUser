package com.javaweb.entity.Payment;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "paymenthistory")
public class PaymentHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long HistoryID;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "Message", length = 500)
    private String message;

    @Column(name = "ResponseData", columnDefinition = "TEXT")
    private String responseData;

    @Column(name = "IPAddress", length = 50)
    private String ipAddress;

    @Column(name = "UserAgent", length = 500)
    private String userAgent;

    @Column(name = "CreatedAt")
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TransactionID")
    private PaymentTransactionEntity paymentTransactionHistory;

    public Long getHistoryID() {
		return HistoryID;
	}

	public void setHistoryID(Long historyID) {
		HistoryID = historyID;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
