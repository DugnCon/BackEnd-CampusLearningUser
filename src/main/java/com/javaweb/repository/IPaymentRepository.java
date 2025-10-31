package com.javaweb.repository;

import com.javaweb.model.dto.PaymentTransactionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javaweb.entity.Payment.PaymentTransactionEntity;

import java.util.List;
import java.util.Set;

@Repository
public interface IPaymentRepository extends JpaRepository<PaymentTransactionEntity, Long>{
    @Query("select pt from PaymentTransactionEntity pt join fetch pt.user u join fetch pt.courses where u.UserID = :userId")
    Set<PaymentTransactionEntity> getPaymentHistory(@Param("userId") Long userId);
    //Cái này sẽ map sang interface nhưng chỉ dùng khi nó thật sự phức tạp và không join sâu
    //Cái này chỉ để học thêm hay test thử
    @Query("""
        select 
            pt.TransactionID as transactionID,
            pt.transactionCode as transactionCode,
            pt.paymentMethod as paymentMethod,
            pt.amount as amount,
            pt.currency as currency,
            pt.paymentStatus as paymentStatus,
            pt.paymentDate as paymentDate,
            c.title as courseTitle
        from PaymentTransactionEntity pt
        join pt.user u
        join pt.courses c
        where u.UserID = :userId and c.CourseID = :courseId
    """)
    List<PaymentTransactionDTO> getCoursePaymentHistory(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
