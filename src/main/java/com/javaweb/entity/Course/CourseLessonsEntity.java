package com.javaweb.entity.Course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.javaweb.entity.Coding.CodingExercisesEntity;

@Entity
@Table(name="courselessons")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class CourseLessonsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long LessonID;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ModuleID")
	@JsonBackReference
	private CourseModuleEntity Modules;
	@Column(name="Title")
	private String title;
	@Column(name="Description")
	private String description;
	@Column(name="Type")
	private String type;
	@Column(name="Duration")
	private Integer duration;
	@Column(name="OrderIndex", nullable = false)
	private Integer orderIndex;
	@Column(name="IsPreview")
	private boolean isPreview;
	@Column(name="VideoUrl")
	private String videoUrl;
	@Column(name="Content")
	private String content;
	@Column(name="CreatedAt", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	@Column(name="UpdatedAt")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	@OneToMany(mappedBy="lessons", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<CodingExercisesEntity> codeExercise = new ArrayList<>();	
	@OneToMany(mappedBy="lessons", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
    private List<LessonProgressEntity> lessonProgress = new ArrayList<>();
	
	
	public List<LessonProgressEntity> getLessonProgress() {
		return lessonProgress;
	}
	public void setLessonProgress(List<LessonProgressEntity> lessonProgress) {
		this.lessonProgress = lessonProgress;
	}
	public List<CodingExercisesEntity> getCodeExercise() {
		return codeExercise;
	}
	public void setCodeExercise(List<CodingExercisesEntity> codeExercise) {
		this.codeExercise = codeExercise;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public Long getLessonID() {
		return LessonID;
	}
	public void setLessonID(Long lessonID) {
		LessonID = lessonID;
	}
	@JsonBackReference
	public CourseModuleEntity getModules() {
		return Modules;
	}
	public void setModules(CourseModuleEntity modules) {
		Modules = modules;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}
	public boolean isPreview() {
		return isPreview;
	}
	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
}
