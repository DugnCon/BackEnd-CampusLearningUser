package com.javaweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javaweb.entity.Payment.PaymentTransactionEntity;
@Repository
public interface IPaymentRepository extends JpaRepository<PaymentTransactionEntity, Long>{

}
