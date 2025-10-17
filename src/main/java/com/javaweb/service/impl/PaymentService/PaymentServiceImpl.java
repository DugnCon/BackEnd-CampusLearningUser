package com.javaweb.service.impl.PaymentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.javaweb.entity.UserEntity;
import com.javaweb.entity.Course.CourseEnrollmentEntity;
import com.javaweb.entity.Course.CourseEntity;
import com.javaweb.entity.Course.CourseLessonsEntity;
import com.javaweb.entity.Course.CourseModuleEntity;
import com.javaweb.entity.Course.LessonProgressEntity;
import com.javaweb.entity.Payment.PaymentTransactionEntity;
import com.javaweb.model.dto.TransactionIDDTO;
import com.javaweb.repository.ICourseEnrollmentRepository;
import com.javaweb.repository.ICourseModulesRepository;
import com.javaweb.repository.ICourseRepository;
import com.javaweb.repository.ILessonProgressRepository;
import com.javaweb.repository.IPaymentRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.IPaymentService;
@Service
public class PaymentServiceImpl implements IPaymentService{
	@Autowired
	private ICourseRepository courseRepository;
	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private IPaymentRepository paymentRepository;
	@Autowired
	private ICourseEnrollmentRepository courseEnrollmentRepository;
	@Autowired
	private ICourseModulesRepository courseModulesRepository;
	@Autowired
	private ILessonProgressRepository lessonProgressRepository;
	
	@Override
	@Transactional(isolation = Isolation.SERIALIZABLE,rollbackFor = Exception.class)//Cấp độ cô lập giao dịch và rollback khi mà transaction sai
	public ResponseEntity<Object> confirmedPayment(TransactionIDDTO transactionDTO) {
		CourseEntity course = courseRepository.findById(transactionDTO.getCourseId()).orElseThrow(() -> new RuntimeException("not found course"));
		UserEntity user = userRepository.findById(transactionDTO.getUserId()).orElseThrow(() -> new RuntimeException("not found user"));
		try {
			
			PaymentTransactionEntity paymentTransaction = new PaymentTransactionEntity();
			paymentTransaction.setCourseTransactions(course);
			paymentTransaction.setUserTransactions(user);
			paymentTransaction.setAmount(120.000);
			paymentTransaction.setCurrency("VND");
			paymentTransaction.setPaymentMethod("paypal");
			paymentTransaction.setTransactionCode(transactionDTO.getTransactionId());
			paymentTransaction.setPaymentStatus("completed");
			paymentTransaction.setPaymentDate(LocalDateTime.now());
			
			paymentRepository.save(paymentTransaction);
			
			insertCourseEnrollment(course,user);
			
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
		    //in stack trace gốc để biết chính xác dòng ném NPE
		    e.printStackTrace();
		    throw new RuntimeException("Can not confirm payment");
		}

	}
	
	@Override
	@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
	public void insertCourseEnrollment(CourseEntity course, UserEntity user) {
		try {
			
			CourseEnrollmentEntity courseEnrollment = new CourseEnrollmentEntity();
			courseEnrollment.setCourseEnrollment(course);
			courseEnrollment.setUserEnrollment(user);
			courseEnrollment.setProgress(0);
			courseEnrollment.setLastAccessedLessonID(null);
			courseEnrollment.setEnrolledAt(LocalDateTime.now());
			courseEnrollment.setCompletedAt(null);
			courseEnrollment.setCertificateIssued(true);
			courseEnrollment.setStatus("active");
			
			courseEnrollmentRepository.save(courseEnrollment);
			
			insertLessonProgress(course,user,courseEnrollment);
			
		} catch (Exception e) {
			throw new RuntimeException(e + "can not insert to course enrollment");
		}
	}
	
	@Override
	@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
	public void insertLessonProgress(CourseEntity course, UserEntity user, CourseEnrollmentEntity courseEnrollment) {
		Set<CourseModuleEntity> module = courseModulesRepository.getModuleForLesson(course.getCourseID());
		List<LessonProgressEntity> progress = new ArrayList<>();
		System.out.println(module);
		
		for(CourseModuleEntity new_module : module) {
			for(CourseLessonsEntity lessons : new_module.getLessons()) {
				LessonProgressEntity data = new LessonProgressEntity();
				data.setEnrollment(courseEnrollment);
				data.setLessons(lessons);
				data.setStatus("not_started");
		        data.setTimeSpent(0);
			    data.setLastPosition(0);
				progress.add(data);
			}
		}
		lessonProgressRepository.saveAll(progress);
	}
	
	@Override
	public ResponseEntity<Object> courseEnrolled(Long userId) {
		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found"));
		try {
			user = userRepository.getCourseEnrollment(userId);
			if(user != null) {
				return ResponseEntity.ok(Map.of("data", user, "success", true, "message", "Đây là khóa học của bạn"));
			} else {
				return ResponseEntity.ok(Map.of("success", false, "message", "Không có khóa học nào"));
			}
		} catch (Exception e) {
			throw new RuntimeException(e + "can not get course enrolled" + " " + userId);
		}
	}

	@Override
	public ResponseEntity<Object> checkCourseEnrollment(Long courseId, Long userId) {
		try {
			CourseEnrollmentEntity courseEnrollment = courseEnrollmentRepository.checkCourseEnrollment(courseId, userId);
			if(courseEnrollment != null) {
				return ResponseEntity.ok(Map.of("success", true, "isEnrolled", true));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "isEnrolled", false));
			}
		} catch(Exception e) {
			throw new RuntimeException(e + " can not check course enrollment");
		}
	}

}
