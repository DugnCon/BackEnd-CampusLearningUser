package com.javaweb.entity.ChatAndCall;

import com.javaweb.entity.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calls")
public class CallEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CallID")
	private Long callID;

	@Column(name = "ConversationID", nullable = false)
	private Long conversationID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "InitiatorID", nullable = false)
	private UserEntity initiator;

	@Column(name = "Type", length = 20, nullable = false)
	private String type; // "audio", "video"

	@Column(name = "StartTime", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "EndTime")
	private LocalDateTime endTime;

	@Column(name = "Status", length = 20, nullable = false)
	private String status; // "initiated", "ringing", "active", "ended", "missed", "rejected"

	@Column(name = "Duration")
	private Integer duration; // in seconds

	@Column(name = "Quality", length = 20)
	private String quality; // "good", "average", "poor"

	@Column(name = "RecordingUrl", length = 255)
	private String recordingUrl;

	// Getters and Setters
	public Long getCallID() {
		return callID;
	}

	public void setCallID(Long callID) {
		this.callID = callID;
	}

	public Long getConversationID() {
		return conversationID;
	}

	public void setConversationID(Long conversationID) {
		this.conversationID = conversationID;
	}

	public UserEntity getInitiator() {
		return initiator;
	}

	public void setInitiator(UserEntity initiator) {
		this.initiator = initiator;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getRecordingUrl() {
		return recordingUrl;
	}

	public void setRecordingUrl(String recordingUrl) {
		this.recordingUrl = recordingUrl;
	}

	// Utility methods
	public void endCall() {
		this.endTime = LocalDateTime.now();
		this.status = "ended";
		if (this.startTime != null && this.endTime != null) {
			this.duration = (int) java.time.Duration.between(startTime, endTime).getSeconds();
		}
	}

	public void markAsActive() {
		this.status = "active";
	}

	public void markAsRinging() {
		this.status = "ringing";
	}

	public void markAsMissed() {
		this.status = "missed";
		this.endTime = LocalDateTime.now();
	}

	public void markAsRejected() {
		this.status = "rejected";
		this.endTime = LocalDateTime.now();
	}

	public boolean isActive() {
		return "active".equals(status);
	}

	public boolean isEnded() {
		return "ended".equals(status) || "missed".equals(status) || "rejected".equals(status);
	}

	public boolean isVideoCall() {
		return "video".equals(type);
	}

	public boolean isAudioCall() {
		return "audio".equals(type);
	}
}