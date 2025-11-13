package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CallDTO {
	private Long callID;
	private Long conversationID;
	private Long initiatorID;
	private String initiatorName;
	private String initiatorPicture;
	private List<CallParticipantDTO> participants;
	private String type; // "audio" or "video"
	private String status; // "calling", "ongoing", "ended", "rejected"
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private Integer duration;// in seconds

}