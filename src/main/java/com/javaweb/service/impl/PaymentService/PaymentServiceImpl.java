package com.javaweb.service.impl.PaymentService;

import java.time.LocalDateTime;
import java.util.*;

import com.javaweb.model.dto.PaymentTransactionDTO;
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
			paymentTransaction.setCourses(course);
			paymentTransaction.setUser(user);
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
		try {
			Set<CourseEnrollmentEntity> enrollmentEntities = courseEnrollmentRepository.getUserCourseEnrolled(userId);
			if(enrollmentEntities.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Bạn chưa đăng kí khóa học nào", "data", new ArrayList<>() ));
			} else {
				Set<CourseEntity> courses = new TreeSet<>(Comparator.comparing(CourseEntity::getCourseID));

				for(CourseEnrollmentEntity data : enrollmentEntities) {
					CourseEntity courseEntity = data.getCourseEnrollment();
					courseEntity.setEnrolled(true);
					courses.add(courseEntity);
				}
				return ResponseEntity.ok(Map.of("success", true, "data", courses));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
	@Override
	public ResponseEntity<Object> getPaymentHistory(Long userId) {
		try {
			Set<PaymentTransactionEntity> paymentTransactionEntities = paymentRepository.getPaymentHistory(userId);
			if(paymentTransactionEntities.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Bạn chưa có cuộc giao dịch nào"));
			} else {
				return ResponseEntity.ok(Map.of("success", true, "message", "Đây là lịch sử cuộc giao dịch", "data", paymentTransactionEntities));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public ResponseEntity<Object> getCoursePaymentHistory(Long userId, Long courseId) {
		try {
			List<PaymentTransactionDTO>	paymentTransactionEntity = paymentRepository.getCoursePaymentHistory(userId, courseId);
			return ResponseEntity.ok(Map.of("data", paymentTransactionEntity, "success", true));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
