package com.javaweb.api.course;

import com.javaweb.entity.Course.CourseEnrollmentEntity;
import com.javaweb.entity.Course.CourseEntity;
import com.javaweb.entity.Course.CourseModuleEntity;
import com.javaweb.model.dto.MyUserDetail;
import com.javaweb.model.dto.PayPalPaymentDTO;
import com.javaweb.model.dto.TransactionIDDTO;
import com.javaweb.repository.ICourseEnrollmentRepository;
import com.javaweb.repository.ICourseModulesRepository;
import com.javaweb.repository.ILessonProgressRepository;
import com.javaweb.service.*;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class CourseAPI {
    @Autowired
    private CodeServerService codeServerService;
    @Autowired
    private ICourseService courseService;
    @Autowired
    private PayPalService payPalService;
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ICourseModulesRepository modules;
    @Autowired
    private ILessonProgressRepository lessonProgressRepository;
    @Autowired
    private ICourseEnrollmentRepository enrollmentRepository;
    @Autowired
    private ILessonProgressService progressService;
    @Autowired
    private ICodingExerciseService codingExerciseService;

    @GetMapping("/courses")
    public ResponseEntity<Object> getCourses() {
        List<CourseEntity> courses = courseService.getAllCourse();
        if(courses.isEmpty()) {
            return ResponseEntity.ok(Map.of("success", false, "data", courses, "message", "Chưa có khóa học nào"));
        }
        return ResponseEntity.ok(Map.of("success", true, "data", courses));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Object> getCourseDetails(@PathVariable("courseId") Long courseId) {
    	//List<CourseModuleEntity> module = courseService.getCourseById(courseId).getCoursemodules();
        return ResponseEntity.ok(Map.of("success", true, "data", courseService.getCourseById(courseId)));
    }

    @GetMapping("/courses/{courseId}/lessons/{lessonId}/code-exercise")
    public ResponseEntity<Object> getCodeExercise(
            @PathVariable("courseId") Long courseId,
            @PathVariable("lessonId") Long lessonId) {
        //return ResponseEntity.ok(Map.of("success", true, "data", courseService.getCodeExercise(courseId, lessonId)));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        return codingExerciseService.getCodingExercise(lessonId);
    }

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/submit-code")
    public ResponseEntity<Object> submitCode(
            @PathVariable("courseId") Long courseId,
            @PathVariable("lessonId") Long lessonId,
            @RequestBody Map<String, Object> submission) {
        System.out.println(codingExerciseService.submitCode(submission, lessonId));
        return codingExerciseService.submitCode(submission, lessonId);
    }

    /*@GetMapping("/{courseId}/psurint-details")
    public ResponseEntity<Object> getCoursePrintDetails(@PathVariable("courseId") String courseId) {
        if (courseId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course ID is required"));
        }
        Object printDetails = courseService.getCoursePrintDetails(courseId);
        return ResponseEntity.ok(Map.of("success", true, "data", printDetails));
    }

    @PostMapping("/code-execution/execute")
    public ResponseEntity<Object> executeCode(@RequestBody Map<String, Object> requestBody) {
        String code = (String) requestBody.get("code");
        String language = (String) requestBody.get("language");
        String stdin = (String) requestBody.getOrDefault("stdin", "");
        if (code == null || language == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Code và ngôn ngữ lập trình là bắt buộc"));
        }
        Object result = courseService.executeCode(code, language, stdin);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @PostMapping("/code-execution/send-input")
    public ResponseEntity<Object> sendInput(@RequestBody Map<String, String> requestBody) {
        String executionId = requestBody.get("executionId");
        String input = requestBody.get("input");
        if (executionId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "ID thực thi là bắt buộc"));
        }
        Object result = courseService.sendInput(executionId, input);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @PostMapping("/code-execution/stop")
    public ResponseEntity<Object> stopExecution(@RequestBody Map<String, String> requestBody) {
        String executionId = requestBody.get("executionId");
        if (executionId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "ID thực thi là bắt buộc"));
        }
        Object result = courseService.stopExecution(executionId);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }*/

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/code-server")
    public ResponseEntity<Object> initializeCodeServer(
            @PathVariable("courseId") Long courseId,
            @PathVariable("lessonId") Long lessonId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

            String userId = myUserDetail.getId().toString();

            if (userId == null || userId.isEmpty()) {
                resp.put("success", false);
                resp.put("message", "Missing userId");
                return ResponseEntity.badRequest().body(resp);
            }

            String url = codeServerService.initialize(courseId.toString(), lessonId.toString(), userId);

            resp.put("success", true);
            resp.put("url", url);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    /*
    @GetMapping("/{courseId}/content")
    public ResponseEntity<Object> getCourseContent(@PathVariable("courseId") String courseId) {
        return ResponseEntity.ok(Map.of("success", true, "data", courseService.getCourseContent(courseId)));
    }

    @PostMapping("/lessons/{lessonId}/progress")
    public ResponseEntity<Object> markLessonComplete(
            @PathVariable("lessonId") String lessonId,
            @RequestParam("userId") String userId) {
        boolean completed = courseService.markLessonAsComplete(lessonId, userId);
        return ResponseEntity.ok(Map.of("success", completed));
    }

    @GetMapping("/{courseId}/lessons/{lessonId}/code-exercise")
    public ResponseEntity<Object> getCodeExercise(
            @PathVariable("courseId") String courseId,
            @PathVariable("lessonId") String lessonId) {
        return ResponseEntity.ok(Map.of("success", true, "data", courseService.getCodeExercise(courseId, lessonId)));
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/run-code")
    public ResponseEntity<Object> runExerciseCode(
            @PathVariable("courseId") String courseId,
            @PathVariable("lessonId") String lessonId,
            @RequestBody Map<String, Object> codeRequest) {
        return ResponseEntity.ok(Map.of("success", true, "output", courseService.runCodeExercise(courseId, lessonId, codeRequest)));
    }

    @PostMapping("/{courseId}/create-payment")
    public ResponseEntity<Object> createPayment(
            @PathVariable("courseId") String courseId,
            @RequestBody Map<String, String> requestBody) {
        String bankCode = requestBody.get("bankCode");
        if (bankCode == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Bank code is required"));
        }
        String paymentUrl = courseService.createPayment(courseId, bankCode);
        return ResponseEntity.ok(Map.of("success", true, "data", paymentUrl));
    }

    @PostMapping("/{courseId}/create-vietqr")
    public ResponseEntity<Object> createVietQRPayment(@PathVariable("courseId") String courseId) {
        Object vietQRData = courseService.createVietQRPayment(courseId);
        return ResponseEntity.ok(Map.of("success", true, "data", vietQRData));
    }

    @PostMapping("/payments/verify-vietqr")
    public ResponseEntity<Object> verifyVietQRPayment(@RequestBody Map<String, String> requestBody) {
        String transactionCode = requestBody.get("transactionCode");
        if (transactionCode == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Transaction code is required"));
        }
        Object verificationResult = courseService.verifyVietQRPayment(transactionCode);
        return ResponseEntity.ok(Map.of("success", true, "data", verificationResult));
    }

    @GetMapping("/payment/vnpay/transaction/{transactionId}")
    public ResponseEntity<Object> processVNPayTransaction(@PathVariable("transactionId") String transactionId) {
        if (transactionId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Transaction ID is required"));
        }
        Object transactionDetails = courseService.processVNPayTransaction(transactionId);
        return ResponseEntity.ok(Map.of("success", true, "data", transactionDetails));
    }*/

    //Tạo đơn hàng Paypal
    @PostMapping("/courses/{courseId}/create-paypal-order")
    public ResponseEntity<Object> createPayPalOrder(@PathVariable("courseId") Long courseId,
                                                    HttpSession session) {
        try {
            double price = courseService.getCoursePrice(courseId);

            PayPalPaymentDTO dto = new PayPalPaymentDTO(
                    price,
                    "USD",
                    "paypal",
                    "sale",
                    "Thanh toán khóa học ID = " + courseId,
                    "http://localhost:8080/api/courses/paypal/cancel",
                    "http://localhost:8080/api/courses/paypal/success?courseId=" + courseId
            );

            Payment payment = payPalService.createPayment(dto);

            for (Links link : payment.getLinks()) {
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "message", "Tạo đơn hàng PayPal thành công",
                            "approveUrl", link.getHref(),
                            "courseId", courseId,
                            "paymentId", payment.getId()
                    ));
                }
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "status", "cancel",
                    "message", "Không tìm thấy approval URL"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "cancel",
                    "message", "Lỗi khi tạo đơn hàng PayPal",
                    "error", e.getMessage()
            ));
        }
    }

    // Callback từ PayPal
    @GetMapping("/courses/paypal/success")
    public RedirectView successPayment(@RequestParam String paymentId,
                                       @RequestParam String PayerID,
                                       @RequestParam String token,
                                       @RequestParam Long courseId) {
        try {
            Payment payment = payPalService.executePayment(paymentId, PayerID);

            if ("approved".equalsIgnoreCase(payment.getState())) {
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl("http://localhost:5004/payment/callback")
                        .queryParam("status", "success")
                        .queryParam("token", token)
                        .queryParam("message", "Thanh toán thành công")
                        .queryParam("courseId", courseId)
                        .queryParam("transactionId", paymentId)
                        .queryParam("PayerID", PayerID);
                return new RedirectView(builder.toUriString());
            } else {
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl("http://localhost:5004/payment/callback")
                        .queryParam("status", "failed")
                        .queryParam("message", "Thanh toán không được chấp thuận");
                return new RedirectView(builder.toUriString());
            }

        } catch (PayPalRESTException e) {
            e.printStackTrace();
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:5004/payment/callback")
                    .queryParam("status", "error")
                    .queryParam("message", "Lỗi khi xác nhận thanh toán: " + e.getMessage());
            return new RedirectView(builder.toUriString());
        }
    }

    // Xử lý POST nội bộ sau callback
    @PostMapping("/payment/paypal/success")
    public ResponseEntity<Object> processPayPalSuccess(@RequestBody Map<String, Object> payload) {
    	
        try {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        	MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();
        	Long userId = myUserDetail.getId();
        	
        	String transactionId = payload.get("transactionId").toString();
        	Long courseId = Long.valueOf(payload.get("courseId").toString());

            TransactionIDDTO transaction = new TransactionIDDTO();
            
    	    transaction.setCourseId(courseId);
            transaction.setTransactionId(transactionId);
            transaction.setUserId(userId);
            
        	return paymentService.confirmedPayment(transaction);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }


    @GetMapping("/courses/paypal/cancel")
    public RedirectView cancelPayment() {
    	UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:5004/payment/paypal/cancel")
                .queryParam("status", "cancel")
                .queryParam("message", "Thanh toán bị hủy");
        return new RedirectView(builder.toUriString());
    }
    
    //Khi mà đăng kí khóa học thành công
    @PostMapping("/{courseId}/enroll/free")
    public ResponseEntity<Object> enrollFreeCourse(
            @PathVariable("courseId") Long courseId) {
        //boolean enrolled = courseService.enrollFreeCourse(courseId, userId);
        //return ResponseEntity.ok(Map.of("success", enrolled, "message", enrolled ? "Đăng ký thành công" : "Đăng ký thất bại"));
    	return null;
    }
    
    //GỬi các khóa học đã đăng kí
    @GetMapping("/courses/enrolled")
    public ResponseEntity<Object> getEnrolledCourses() {
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();
    	
    	Long userId = myUserDetail.getId();

    	return paymentService.courseEnrolled(userId);
    }
    
    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<Object> getUserProgress(
            @PathVariable("courseId") Long courseId) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return courseService.getUserProgress(courseId, userId);
    }

    @GetMapping("/enrollments")
    public ResponseEntity<Object> getUserCourseEnrolled() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        return courseService.getUserCourseEnrolled(userId);
    }

    //Đợi thêm vào security
    @PostMapping("/lessons/{lessonId}/progress")
    private ResponseEntity<Object> lessonCompleted(@RequestBody Map<String,Object> data, @PathVariable Long lessonId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

        String status = data.get("status").toString();

        return progressService.lessonCompleted(status, lessonId, userId);
    }
    
    @GetMapping("/courses/{courseId}/check-enrollment")
    public ResponseEntity<Object> checkEnrollment(
            @PathVariable("courseId") Long courseId) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();
    	
    	Long userId = myUserDetail.getId();
       // boolean enrolled = courseService.isUserEnrolled(courseId, userId);
       // return ResponseEntity.ok(Map.of("success", true, "enrolled", enrolled));
    	return paymentService.checkCourseEnrollment(courseId, userId);
    }

    @GetMapping("/user/payment-history")
    public ResponseEntity<Object> getPaymentHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

    	return paymentService.getPaymentHistory(userId);
    }

    //Tí nữa làm cái này
    @GetMapping("/courses/{courseId}/payment-history")
    public ResponseEntity<Object> getCoursePaymentHistory(@PathVariable("courseId") Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail myUserDetail = (MyUserDetail) auth.getPrincipal();

        Long userId = myUserDetail.getId();

    	return paymentService.getCoursePaymentHistory(userId, courseId);
    }

    /*@DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<Object> deletePayment(@PathVariable("paymentId") String paymentId) {
        if (paymentId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Payment ID is required"));
        }
        boolean success = courseService.deletePayment(paymentId);
        return ResponseEntity.ok(Map.of("success", success, "message", success ? "Xóa giao dịch thành công" : "Xóa giao dịch thất bại"));
    }

    @PostMapping("/payments/delete-many")
    public ResponseEntity<Object> deleteManyPayments(@RequestBody Map<String, List<String>> requestBody) {
        List<String> paymentIds = requestBody.get("paymentIds");
        if (paymentIds == null || paymentIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Valid payment IDs array is required"));
        }
        boolean success = courseService.deleteManyPayments(paymentIds);
        return ResponseEntity.ok(Map.of("success", success, "message", success ? "Xóa nhiều giao dịch thành công" : "Xóa nhiều giao dịch thất bại"));
    }

    @GetMapping("/{courseId}/print-details")
    public ResponseEntity<Object> getCoursePrintDetails(@PathVariable("courseId") String courseId) {
        if (courseId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Course ID is required"));
        }
        Object printDetails = courseService.getCoursePrintDetails(courseId);
        return ResponseEntity.ok(Map.of("success", true, "data", printDetails));
    }

    @PostMapping("/code-execution/execute")
    public ResponseEntity<Object> executeCode(@RequestBody Map<String, Object> requestBody) {
        String code = (String) requestBody.get("code");
        String language = (String) requestBody.get("language");
        String stdin = (String) requestBody.getOrDefault("stdin", "");
        if (code == null || language == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Code và ngôn ngữ lập trình là bắt buộc"));
        }
        Object result = courseService.executeCode(code, language, stdin);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @PostMapping("/code-execution/send-input")
    public ResponseEntity<Object> sendInput(@RequestBody Map<String, String> requestBody) {
        String executionId = requestBody.get("executionId");
        String input = requestBody.get("input");
        if (executionId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "ID thực thi là bắt buộc"));
        }
        Object result = courseService.sendInput(executionId, input);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @PostMapping("/code-execution/stop")
    public ResponseEntity<Object> stopExecution(@RequestBody Map<String, String> requestBody) {
        String executionId = requestBody.get("executionId");
        if (executionId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "ID thực thi là bắt buộc"));
        }
        Object result = courseService.stopExecution(executionId);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/code-server")
    public ResponseEntity<Object> initializeCodeServer(
            @PathVariable("courseId") String courseId,
            @PathVariable("lessonId") String lessonId) {
        Object result = courseService.initializeCodeServer(courseId, lessonId);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }*/
}
