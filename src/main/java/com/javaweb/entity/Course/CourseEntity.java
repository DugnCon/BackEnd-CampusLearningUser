package com.javaweb.entity.Course;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.javaweb.entity.Payment.PaymentTransactionEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

/*@NamedStoredProcedureQuery(
		name="CourseEntity.getAllCourse",
		procedureName="all_course",
		resultClasses = CourseEntity.class
)*/
@Entity
@Table(name="courses")
public class CourseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long CourseID;
	@Column(name="Title")
	private String title;
	@Column(name="Description")
	private String description;
	@Column(name="Level")
	private String level;
	@Column(name="Category")
	private String category;
	@Column(name="Language")
	private String language;
	@Column(name="Duration")
	private Integer duration;
	@Column(name="Capacity")
	private Integer capacity;
	@Column(name="Price")
	private Double price;
	@Column(name="Requirements")
	private String requirements;
	@Column(name="Objectives")
	private String objectives;
	@Column(name="Status")
	private String status;
	@Column(name="Syllabus")
	private String syllabus;
	@Column(name="ImageUrl")
	private String imageUrl;
	@Column(name="VideoUrl")
	private String videoUrl;
	@Column(name="CourseType")
	private String courseType;
	@OneToMany(mappedBy = "courses", cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
	private List<CourseModuleEntity> Modules = new ArrayList<>();
	@OneToMany(mappedBy = "courses", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<PaymentTransactionEntity> paymentTransaction = new ArrayList<>();
	@OneToMany(mappedBy="courseEnrollment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<CourseEnrollmentEntity> courseEnrollment = new ArrayList<>();
	@Transient
	private boolean enrolled;

	public boolean isEnrolled() {
		return enrolled;
	}

	public void setEnrolled(boolean enrolled) {
		this.enrolled = enrolled;
	}

	public List<CourseEnrollmentEntity> getCourseEnrollment() {
		return courseEnrollment;
	}

	public void setCourseEnrollment(List<CourseEnrollmentEntity> courseEnrollment) {
		this.courseEnrollment = courseEnrollment;
	}

	public List<PaymentTransactionEntity> getPaymentTransaction() {
		return paymentTransaction;
	}

	public void setPaymentTransaction(List<PaymentTransactionEntity> paymentTransaction) {
		this.paymentTransaction = paymentTransaction;
	}

	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}

	public Long getCourseID() {
		return CourseID;
	}
	public void setCourseID(Long courseID) {
		CourseID = courseID;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public String getRequirements() {
		return requirements;
	}
	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}
	public String getObjectives() {
		return objectives;
	}
	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}
	public String getSyllabus() {
		return syllabus;
	}
	public void setSyllabus(String syllabus) {
		this.syllabus = syllabus;
	}
	
	public List<CourseModuleEntity> getModules() {
		return Modules;
	}

	public void setModules(List<CourseModuleEntity> modules) {
		Modules = modules;
	}

	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
