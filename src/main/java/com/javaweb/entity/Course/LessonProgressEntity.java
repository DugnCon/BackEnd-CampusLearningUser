package com.javaweb.entity.Course;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="lessonprogress")
@DynamicUpdate
public class LessonProgressEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ProgressID;
	//enrollmentId
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LessonID")
	@JsonBackReference
	private CourseLessonsEntity lessons;
	//lessonId
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="EnrollmentID")
	@JsonBackReference
	private CourseEnrollmentEntity enrollment;
	@Column(name="Status")
	private String status;
	@Column(name="CompletedAt")
	private LocalDateTime completedAt;
	@Column(name="TimeSpent")
	private Integer timeSpent;
	@Column(name="LastPosition")
	private Integer lastPosition;
	
	public Long getProgressID() {
		return ProgressID;
	}
	public void setProgressID(Long progressID) {
		ProgressID = progressID;
	}
	@JsonBackReference
	public CourseLessonsEntity getLessons() {
		return lessons;
	}
	public void setLessons(CourseLessonsEntity lessons) {
		this.lessons = lessons;
	}
	@JsonBackReference
	public CourseEnrollmentEntity getEnrollment() {
		return enrollment;
	}
	public void setEnrollment(CourseEnrollmentEntity enrollment) {
		this.enrollment = enrollment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getCompletedAt() {
		return completedAt;
	}
	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}
	public Integer getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(Integer timeSpent) {
		this.timeSpent = timeSpent;
	}
	public Integer getLastPosition() {
		return lastPosition;
	}
	public void setLastPosition(Integer lastPosition) {
		this.lastPosition = lastPosition;
	}
	
}
