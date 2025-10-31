package com.javaweb.entity.Event;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.javaweb.entity.UserEntity;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventparticipants")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class EventParticipantsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ParticipantID")
    private Long participantID;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="eventID")
    @JsonBackReference
    private EventEntity event;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UserID")
    @JsonBackReference
    private UserEntity user;
    @Column(name="RegistrationDate")
    @CreationTimestamp
    private LocalDateTime registrationDate;
    @Column(name = "Status")
    private String status;
    @Column(name = "TeamName")
    private String teamName;
    @Column(name = "PaymentStatus")
    private String paymentStatus;
    @Column(name = "AttendanceStatus")
    private String attendanceStatus;
    public Long getParticipantID() {
        return participantID;
    }

    public void setParticipantID(Long participantID) {
        this.participantID = participantID;
    }
    @JsonBackReference
    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }
    @JsonBackReference
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

}
