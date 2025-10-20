package com.javaweb.entity.Event;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "events")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EventID")
    private Long eventID;

    @Column(name = "Title", length = 255)
    private String title;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Category", length = 50)
    private String category;

    @Column(name = "EventDate")
    private LocalDate eventDate;

    @Column(name = "EventTime")
    private LocalTime eventTime;

    @Column(name = "Location", length = 255)
    private String location;

    @Column(name = "ImageUrl", length = 500)
    private String imageUrl;

    @Column(name = "MaxAttendees")
    private Integer maxAttendees;

    @Column(name = "CurrentAttendees")
    private Integer currentAttendees;

    @Column(name = "Price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "Organizer", length = 255)
    private String organizer;

    @Column(name = "Difficulty", length = 20)
    private String difficulty;

    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "CreatedBy")
    private Long createdBy;

    @CreatedDate //annotation này là ngày được tạo
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate //annotation này là lần cuối chỉnh sửa
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<EventScheduleEntity> schedule = new TreeSet<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<EventPrizesEntity> prizes = new TreeSet<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<EventProgrammingLanguagesEntity> languages = new TreeSet<>();

    @OneToMany(mappedBy = "event" , fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<EventTechnologiesEntity> technologies = new TreeSet<>();

    public Set<EventTechnologiesEntity> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<EventTechnologiesEntity> technologies) {
        this.technologies = technologies;
    }

    public Set<EventProgrammingLanguagesEntity> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<EventProgrammingLanguagesEntity> languages) {
        this.languages = languages;
    }

    public Set<EventPrizesEntity> getPrizes() {
        return prizes;
    }

    public void setPrizes(Set<EventPrizesEntity> prizes) {
        this.prizes = prizes;
    }

    public Set<EventScheduleEntity> getSchedule() {
        return schedule;
    }

    public void setSchedule(Set<EventScheduleEntity> schedule) {
        this.schedule = schedule;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Integer getCurrentAttendees() {
        return currentAttendees;
    }

    public void setCurrentAttendees(Integer currentAttendees) {
        this.currentAttendees = currentAttendees;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
