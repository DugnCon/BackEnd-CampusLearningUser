package com.javaweb.entity.Payment;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.Course.CourseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paymenttransactions")
public class PaymentTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long TransactionID;
    
    @Column(name = "Amount", precision = 10, scale = 2, nullable = false)
    private Double amount;

    @Column(name = "Currency", length = 10, nullable = false)
    private String currency;

    @Column(name = "PaymentMethod", length = 50)
    private String paymentMethod;

    @Column(name = "TransactionCode", length = 100)
    private String transactionCode;

    @Column(name = "PaymentStatus", length = 20)
    private String paymentStatus;

    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "CreatedAt")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "PaymentDetails", columnDefinition = "TEXT")
    private String paymentDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UserID")
    @JsonBackReference
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CourseID")
    @JsonManagedReference
    private CourseEntity courses;
    
    @OneToMany(mappedBy = "paymentTransactionHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PaymentHistoryEntity> paymentHistory = new ArrayList<>();
    
    
	public List<PaymentHistoryEntity> getPaymentHistory() {
		return paymentHistory;
	}

	public void setPaymentHistory(List<PaymentHistoryEntity> paymentHistory) {
		this.paymentHistory = paymentHistory;
	}

    public CourseEntity getCourses() {
        return courses;
    }

    public void setCourses(CourseEntity courses) {
        this.courses = courses;
    }

    public Long getTransactionID() {
		return TransactionID;
	}

	public void setTransactionID(Long transactionID) {
		TransactionID = transactionID;
	}

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
}

