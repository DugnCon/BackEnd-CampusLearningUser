package com.javaweb.entity.Course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

//ManyToOne
@Entity
@Table(name="coursemodules")
public class CourseModuleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ModuleID;
	@Column(name="Title")
	private String title;
	@Column(name="Description")
	private String description;
	@Column(name="VideoUrl")
	private String videoUrl;
	@Column(name="ImageUrl")
	private String ImageUrl;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CourseID")
	@JsonBackReference
	private CourseEntity courses;
	@OneToMany(mappedBy = "Modules", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<CourseLessonsEntity> lessons = new ArrayList<>();
	
	
	
	@JsonManagedReference
	
	public String getImageUrl() {
		return ImageUrl;
	}
	public List<CourseLessonsEntity> getLessons() {
		return lessons;
	}
	public void setLessons(List<CourseLessonsEntity> lessons) {
		this.lessons = lessons;
	}
	public void setImageUrl(String imageUrl) {
		this.ImageUrl = imageUrl;
	}
	public Long getModuleID() {
		return ModuleID;
	}
	public void setModuleID(Long moduleID) {
		this.ModuleID = moduleID;
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
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public CourseEntity getCourses() {
		return courses;
	}
	public void setCourses(CourseEntity courses) {
		this.courses = courses;
	}
}
