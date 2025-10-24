package com.javaweb.entity.Course;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.javaweb.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courseenrollments")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class CourseEnrollmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long EnrollmentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CourseID")
    @JsonManagedReference
    private CourseEntity courseEnrollment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UserID")
    private UserEntity userEnrollment;
    
    @OneToMany(mappedBy="enrollment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("enroll-progress")
    private List<LessonProgressEntity> lessonProgress = new ArrayList<>();

    @Column(name = "Progress")
    private Integer progress;

    @Column(name = "LastAccessedLessonID")
    private Long lastAccessedLessonID;

    @Column(name = "EnrolledAt")
    private LocalDateTime enrolledAt;

    @Column(name = "CompletedAt")
    private LocalDateTime completedAt;

    @Column(name = "CertificateIssued")
    private Boolean certificateIssued;

    @Column(name = "Status", length = 20)
    private String status;

    @JsonManagedReference("enroll-progress")
    public List<LessonProgressEntity> getLessonProgress() {
		return lessonProgress;
	}
	public void setLessonProgress(List<LessonProgressEntity> lessonProgress) {
		this.lessonProgress = lessonProgress;
	}
	public Long getEnrollmentID() {
		return EnrollmentID;
	}
	public void setEnrollmentID(Long enrollmentID) {
		EnrollmentID = enrollmentID;
	}
	@JsonManagedReference
    public CourseEntity getCourseEnrollment() {
		return courseEnrollment;
	}
	public void setCourseEnrollment(CourseEntity courseEnrollment) {
		this.courseEnrollment = courseEnrollment;
	}
	@JsonBackReference
	public UserEntity getUserEnrollment() {
		return userEnrollment;
	}
	public void setUserEnrollment(UserEntity userEnrollment) {
		this.userEnrollment = userEnrollment;
	}
	public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Long getLastAccessedLessonID() { return lastAccessedLessonID; }
    public void setLastAccessedLessonID(Long lastAccessedLessonID) { this.lastAccessedLessonID = lastAccessedLessonID; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Boolean getCertificateIssued() { return certificateIssued; }
    public void setCertificateIssued(Boolean certificateIssued) { this.certificateIssued = certificateIssued; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
