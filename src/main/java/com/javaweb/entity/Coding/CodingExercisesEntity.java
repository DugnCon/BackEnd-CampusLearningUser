package com.javaweb.entity.Coding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.javaweb.converter.TestCaseConverter;
import com.javaweb.entity.Course.CourseLessonsEntity;
import com.javaweb.model.dto.TestCasesDTO;

@Entity
@Table(name="codingexercises")
@EntityListeners(AuditingEntityListener.class)
public class CodingExercisesEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ExerciseID;
	@Column(name="Title")
	private String title;
	@Column(name="Description")
	private String description;
	@Column(name="ProgrammingLanguage")
	private String programmingLanguage;
	@Column(name="InitialCode")
	private String initialCode;
	@Column(name="SolutionCode")
	private String solutionCode;
	@Convert(converter = TestCaseConverter.class)
	@Column(name="TestCases")
	private List<TestCasesDTO> testCases;
	@Column(name="TimeLimit")
	private Integer timeLimit;
	@Column(name="MemoryLimit")
	private Integer memoryLimit;
	@Column(name="Difficulty")
	private String difficulty;
	@Column(name="Points")
	private Integer points;
	@Column(name="CreatedAt", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	@Column(name="UpdatedAt")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LessonID")
	@JsonBackReference
	private CourseLessonsEntity lessons;
	
	@JsonBackReference
	public CourseLessonsEntity getLessons() {
		return lessons;
	}
	public void setLessons(CourseLessonsEntity lessons) {
		this.lessons = lessons;
	}
	public List<TestCasesDTO> getTestCases() {
		return testCases;
	}
	public void setTestCases(List<TestCasesDTO> testCases) {
		this.testCases = testCases;
	}
	public Long getExerciseID() {
		return ExerciseID;
	}
	public void setExerciseID(Long exerciseID) {
		ExerciseID = exerciseID;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
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
	public String getProgrammingLanguage() {
		return programmingLanguage;
	}
	public void setProgrammingLanguage(String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}
	public String getInitialCode() {
		return initialCode;
	}
	public void setInitialCode(String initialCode) {
		this.initialCode = initialCode;
	}
	public String getSolutionCode() {
		return solutionCode;
	}
	public void setSolutionCode(String solutionCode) {
		this.solutionCode = solutionCode;
	}
	public Integer getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}
	public Integer getMemoryLimit() {
		return memoryLimit;
	}
	public void setMemoryLimit(Integer memoryLimit) {
		this.memoryLimit = memoryLimit;
	}
	public String getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
