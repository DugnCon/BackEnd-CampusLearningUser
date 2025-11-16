package com.javaweb.service.impl.CallService;

import com.javaweb.entity.ChatAndCall.CallEntity;
import com.javaweb.entity.ChatAndCall.CallParticipantEntity;
import com.javaweb.entity.ChatAndCall.ConversationEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.model.dto.ChatAndCall.CallDTO;
import com.javaweb.model.dto.ChatAndCall.CallParticipantDTO;
import com.javaweb.model.dto.ChatAndCall.InitiateCallRequest;
import com.javaweb.repository.ICallParticipantRepository;
import com.javaweb.repository.ICallRepository;
import com.javaweb.repository.IConversationRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.ICallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CallServiceImpl implements ICallService {

	@Autowired
	private ICallRepository callRepository;

	@Autowired
	private ICallParticipantRepository callParticipantRepository;

	@Autowired
	private IConversationRepository conversationRepository;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Override
	public CallDTO initiateCall(InitiateCallRequest request, Long userId) {
		try {
			log.info("INITIATE CALL - conversationId: {}, type: {}, userId: {}",
					request.getConversationID(), request.getType(), userId);

			if (request.getConversationID() == null || request.getType() == null) {
				throw new IllegalArgumentException("Thiếu conversationId hoặc type");
			}

			if (!request.getType().equals("audio") && !request.getType().equals("video")) {
				throw new IllegalArgumentException("Type phải là 'audio' hoặc 'video'");
			}

			ConversationEntity conversation = conversationRepository.findById(request.getConversationID())
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy conversation với ID: " + request.getConversationID()));

			UserEntity initiator = userRepository.findById(userId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user initiator"));

			CallEntity callEntity = new CallEntity();
			callEntity.setConversation(conversation);
			callEntity.setInitiator(initiator);
			callEntity.setType(request.getType());
			callEntity.setStatus("initiated");
			callEntity.setStartTime(LocalDateTime.now());

			CallEntity savedCall = callRepository.save(callEntity);
			log.info("CALL ENTITY CREATED - callId: {}", savedCall.getCallID());

			createCallParticipants(savedCall, conversation, initiator);

			CallDTO callDTO = convertToDTO(savedCall);

			notifyCallInitiated(callDTO, userId);

			log.info("INITIATE CALL SUCCESS - callId: {}, participants: {}",
					savedCall.getCallID(), callDTO.getParticipants().size());

			return callDTO;

		} catch (Exception e) {
			log.error("INITIATE CALL ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể khởi tạo cuộc gọi: " + e.getMessage());
		}
	}

	@Override
	public void notifyCallInitiated(CallDTO call, Long initiatorId) {
		try {
			log.info("SENDING CALL INITIATE NOTIFICATION - callId: {}, initiator: {}",
					call.getCallID(), initiatorId);

			Map<String, Object> message = new HashMap<>();
			message.put("type", "CALL_INITIATED");
			message.put("data", Map.of(
					"callId", call.getCallID(),
					"conversationId", call.getConversationID(),
					"type", call.getType(),
					"initiatorId", initiatorId,
					"initiatorName", getInitiatorName(call),
					"participants", call.getParticipants(),
					"timestamp", System.currentTimeMillis()
			));
			message.put("timestamp", System.currentTimeMillis());

			for (CallParticipantDTO participant : call.getParticipants()) {
				if (!participant.getUserID().equals(initiatorId)) {
					messagingTemplate.convertAndSendToUser(
							participant.getUserID().toString(),
							"/queue/call-invite",
							message
					);
					log.info("CALL INVITE SENT TO USER: {}", participant.getUserID());
				}
			}

			messagingTemplate.convertAndSend(
					"/topic/conversation." + call.getConversationID() + ".call",
					message
			);

			log.info("CALL INITIATE NOTIFICATION SENT SUCCESSFULLY");

		} catch (Exception e) {
			log.error("ERROR SENDING CALL INITIATE NOTIFICATION: {}", e.getMessage(), e);
		}
	}

	@Override
	public CallDTO answerCall(Long callId, Long userId) {
		try {
			log.info("ANSWER CALL - callId: {}, userId: {}", callId, userId);

			CallEntity call = callRepository.findById(callId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc gọi với ID: " + callId));

			if (!isParticipant(callId, userId)) {
				throw new IllegalArgumentException("Bạn không phải là participant của cuộc gọi này");
			}

			if (call.isEnded()) {
				throw new IllegalArgumentException("Cuộc gọi đã kết thúc");
			}

			call.markAsActive();
			updateParticipantStatus(call.getCallID(), userId, "joined");

			CallEntity updatedCall = callRepository.save(call);
			CallDTO callDTO = convertToDTO(updatedCall);

			notifyCallAnswered(callDTO, userId);

			log.info("ANSWER CALL SUCCESS - callId: {}", callId);
			return callDTO;

		} catch (Exception e) {
			log.error("ANSWER CALL ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể trả lời cuộc gọi: " + e.getMessage());
		}
	}

	@Override
	public CallDTO endCall(Long callId, String reason, Long userId) {
		try {
			log.info("END CALL - callId: {}, reason: {}, userId: {}", callId, reason, userId);

			CallEntity call = callRepository.findById(callId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc gọi với ID: " + callId));

			call.endCall();
			updateAllParticipantsStatus(call.getCallID(), "left");

			CallEntity endedCall = callRepository.save(call);
			CallDTO callDTO = convertToDTO(endedCall);

			notifyCallEnded(callDTO, userId, reason);

			log.info("END CALL SUCCESS - callId: {}, duration: {}s", callId, callDTO.getDuration());
			return callDTO;

		} catch (Exception e) {
			log.error("END CALL ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể kết thúc cuộc gọi: " + e.getMessage());
		}
	}

	@Override
	public CallDTO rejectCall(Long callId, Long userId) {
		try {
			log.info("REJECT CALL - callId: {}, userId: {}", callId, userId);

			CallEntity call = callRepository.findById(callId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc gọi với ID: " + callId));

			call.markAsRejected();
			updateParticipantStatus(call.getCallID(), userId, "declined");

			CallEntity rejectedCall = callRepository.save(call);
			CallDTO callDTO = convertToDTO(rejectedCall);

			notifyCallRejected(callDTO, userId);

			log.info("REJECT CALL SUCCESS - callId: {}", callId);
			return callDTO;

		} catch (Exception e) {
			log.error("REJECT CALL ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể từ chối cuộc gọi: " + e.getMessage());
		}
	}

	@Override
	public CallDTO getCallById(Long callId, Long userId) {
		try {
			log.info("GET CALL BY ID - callId: {}, userId: {}", callId, userId);

			CallEntity call = callRepository.findById(callId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc gọi với ID: " + callId));

			return convertToDTO(call);

		} catch (Exception e) {
			log.error("GET CALL BY ID ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể lấy thông tin cuộc gọi: " + e.getMessage());
		}
	}

	@Override
	public List<CallDTO> getActiveCalls() {
		try {
			log.info("GET ACTIVE CALLS");

			List<CallEntity> activeCalls = callRepository.findActiveCalls();
			List<CallDTO> result = activeCalls.stream()
					.map(this::convertToDTO)
					.collect(Collectors.toList());

			log.info("GET ACTIVE CALLS SUCCESS - count: {}", result.size());
			return result;

		} catch (Exception e) {
			log.error("GET ACTIVE CALLS ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể lấy danh sách cuộc gọi active: " + e.getMessage());
		}
	}

	// ========== WEBSOCKET NOTIFICATION METHODS ========== //

	private void notifyCallAnswered(CallDTO call, Long respondentId) {
		try {
			log.info("SENDING CALL ANSWERED NOTIFICATION - callId: {}, respondent: {}",
					call.getCallID(), respondentId);

			UserEntity respondent = userRepository.findById(respondentId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user"));

			Map<String, Object> message = new HashMap<>();
			message.put("type", "CALL_ANSWERED");
			message.put("data", Map.of(
					"callId", call.getCallID(),
					"conversationId", call.getConversationID(),
					"respondentId", respondentId,
					"respondentName", respondent.getFullName(),
					"respondentPicture", respondent.getAvatar(),
					"callStatus", call.getStatus(),
					"callType", call.getType(),
					"participants", call.getParticipants(),
					"timestamp", System.currentTimeMillis()
			));
			message.put("timestamp", System.currentTimeMillis());

			for (CallParticipantDTO participant : call.getParticipants()) {
				if (!participant.getUserID().equals(respondentId)) {
					messagingTemplate.convertAndSendToUser(
							participant.getUserID().toString(),
							"/queue/call-events",
							message
					);
					log.info("CALL ANSWERED NOTIFICATION SENT TO USER: {}", participant.getUserID());
				}
			}

			log.info("CALL ANSWERED NOTIFICATION SENT SUCCESSFULLY");

		} catch (Exception e) {
			log.error("ERROR SENDING CALL ANSWERED NOTIFICATION: {}", e.getMessage(), e);
		}
	}

	private void notifyCallEnded(CallDTO call, Long endedById, String reason) {
		try {
			log.info("SENDING CALL ENDED NOTIFICATION - callId: {}, endedBy: {}, reason: {}",
					call.getCallID(), endedById, reason);

			UserEntity endedByUser = userRepository.findById(endedById)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user"));

			Map<String, Object> message = new HashMap<>();
			message.put("type", "CALL_ENDED");
			message.put("data", Map.of(
					"callId", call.getCallID(),
					"conversationId", call.getConversationID(),
					"endedById", endedById,
					"endedByName", endedByUser.getFullName(),
					"reason", reason,
					"duration", call.getDuration(),
					"callStatus", call.getStatus(),
					"timestamp", System.currentTimeMillis()
			));
			message.put("timestamp", System.currentTimeMillis());

			for (CallParticipantDTO participant : call.getParticipants()) {
				messagingTemplate.convertAndSendToUser(
						participant.getUserID().toString(),
						"/queue/call-events",
						message
				);
				log.info("CALL ENDED NOTIFICATION SENT TO USER: {}", participant.getUserID());
			}

			log.info("CALL ENDED NOTIFICATION SENT SUCCESSFULLY");

		} catch (Exception e) {
			log.error("ERROR SENDING CALL ENDED NOTIFICATION: {}", e.getMessage(), e);
		}
	}

	private void notifyCallRejected(CallDTO call, Long rejectedById) {
		try {
			log.info("SENDING CALL REJECTED NOTIFICATION - callId: {}, rejectedBy: {}",
					call.getCallID(), rejectedById);

			UserEntity rejectedByUser = userRepository.findById(rejectedById)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user"));

			Map<String, Object> message = new HashMap<>();
			message.put("type", "CALL_REJECTED");
			message.put("data", Map.of(
					"callId", call.getCallID(),
					"conversationId", call.getConversationID(),
					"rejectedById", rejectedById,
					"rejectedByName", rejectedByUser.getFullName(),
					"callStatus", call.getStatus(),
					"timestamp", System.currentTimeMillis()
			));
			message.put("timestamp", System.currentTimeMillis());

			for (CallParticipantDTO participant : call.getParticipants()) {
				if (!participant.getUserID().equals(rejectedById)) {
					messagingTemplate.convertAndSendToUser(
							participant.getUserID().toString(),
							"/queue/call-events",
							message
					);
					log.info("CALL REJECTED NOTIFICATION SENT TO USER: {}", participant.getUserID());
				}
			}

			log.info("CALL REJECTED NOTIFICATION SENT SUCCESSFULLY");

		} catch (Exception e) {
			log.error("ERROR SENDING CALL REJECTED NOTIFICATION: {}", e.getMessage(), e);
		}
	}

	// ========== HELPER METHODS ========== //

	private String getInitiatorName(CallDTO call) {
		return call.getParticipants().stream()
				.filter(p -> p.getUserID().equals(call.getInitiatorID()))
				.findFirst()
				.map(CallParticipantDTO::getFullName)
				.orElse("Unknown");
	}

	private boolean isParticipant(Long callId, Long userId) {
		return callParticipantRepository.existsByCallCallIDAndUserUserID(callId, userId);
	}

	private void createCallParticipants(CallEntity call, ConversationEntity conversation, UserEntity initiator) {
		try {
			List<CallParticipantEntity> participants = new ArrayList<>();

			CallParticipantEntity initiatorParticipant = new CallParticipantEntity();
			initiatorParticipant.setCall(call);
			initiatorParticipant.setUser(initiator);
			initiatorParticipant.setJoinTime(new Date());
			initiatorParticipant.setStatus("joined");
			participants.add(initiatorParticipant);

			List<UserEntity> allUsers = userRepository.findAll();
			Optional<UserEntity> otherUserOpt = allUsers.stream()
					.filter(u -> !u.getUserID().equals(initiator.getUserID()))
					.findFirst();

			if (otherUserOpt.isPresent()) {
				UserEntity otherUser = otherUserOpt.get();
				CallParticipantEntity otherParticipant = new CallParticipantEntity();
				otherParticipant.setCall(call);
				otherParticipant.setUser(otherUser);
				otherParticipant.setStatus("invited");
				participants.add(otherParticipant);
			}

			callParticipantRepository.saveAll(participants);
			call.setCallParticipant(participants);

			log.info("CREATED PARTICIPANTS - callId: {}, count: {}", call.getCallID(), participants.size());

		} catch (Exception e) {
			log.error("CREATE PARTICIPANTS ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể tạo participants cho cuộc gọi: " + e.getMessage());
		}
	}

	private void updateParticipantStatus(Long callId, Long userId, String status) {
		try {
			Optional<CallParticipantEntity> participantOpt = callParticipantRepository
					.findByCallAndUser(callId, userId);

			if (participantOpt.isPresent()) {
				CallParticipantEntity participant = participantOpt.get();
				String mappedStatus = mapStatusToConstraint(status);
				participant.setStatus(mappedStatus);

				if ("joined".equals(mappedStatus)) {
					participant.setJoinTime(new Date());
				} else if ("left".equals(mappedStatus) || "declined".equals(mappedStatus)) {
					participant.setLeaveTime(new Date());
				}

				callParticipantRepository.save(participant);
				log.info("UPDATED PARTICIPANT STATUS - callId: {}, userId: {}, status: {}",
						callId, userId, mappedStatus);
			} else {
				log.warn("PARTICIPANT NOT FOUND - callId: {}, userId: {}", callId, userId);
				throw new IllegalArgumentException("Không tìm thấy participant");
			}

		} catch (Exception e) {
			log.error("UPDATE PARTICIPANT STATUS ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể cập nhật trạng thái participant: " + e.getMessage());
		}
	}

	private void updateAllParticipantsStatus(Long callId, String status) {
		try {
			List<CallParticipantEntity> participants = callParticipantRepository.findByCallId(callId);

			for (CallParticipantEntity participant : participants) {
				if (!"left".equals(participant.getStatus()) && !"declined".equals(participant.getStatus())) {
					String mappedStatus = mapStatusToConstraint(status);
					participant.setStatus(mappedStatus);
					participant.setLeaveTime(new Date());
				}
			}

			callParticipantRepository.saveAll(participants);
			log.info("UPDATED ALL PARTICIPANTS STATUS - callId: {}, status: {}", callId, status);

		} catch (Exception e) {
			log.error("UPDATE ALL PARTICIPANTS STATUS ERROR: {}", e.getMessage(), e);
		}
	}

	private String mapStatusToConstraint(String status) {
		switch (status.toLowerCase()) {
			case "joined":
			case "active":
			case "answered":
				return "joined";
			case "calling":
			case "pending":
			case "ringing":
				return "invited";
			case "left":
			case "ended":
			case "completed":
				return "left";
			case "rejected":
			case "declined":
			case "cancelled":
				return "declined";
			default:
				return "invited";
		}
	}

	private CallDTO convertToDTO(CallEntity entity) {
		CallDTO dto = new CallDTO();
		dto.setCallID(entity.getCallID());
		dto.setConversationID(entity.getConversation().getConversationID());
		dto.setInitiatorID(entity.getInitiator().getUserID());
		dto.setInitiatorName(entity.getInitiator().getFullName());
		dto.setInitiatorPicture(entity.getInitiator().getAvatar());
		dto.setType(entity.getType());
		dto.setStatus(entity.getStatus());
		dto.setStartedAt(entity.getStartTime());
		dto.setEndedAt(entity.getEndTime());
		dto.setDuration(entity.getDuration());

		List<CallParticipantDTO> participantDTOs = new ArrayList<>();
		if (entity.getCallParticipant() != null) {
			for (CallParticipantEntity participant : entity.getCallParticipant()) {
				CallParticipantDTO participantDTO = new CallParticipantDTO();
				participantDTO.setUserID(participant.getUser().getUserID());
				participantDTO.setUsername(participant.getUser().getUsername());
				participantDTO.setFullName(participant.getUser().getFullName());
				participantDTO.setProfilePicture(participant.getUser().getAvatar());
				participantDTO.setStatus(participant.getStatus());
				participantDTO.setJoinedAt(participant.getJoinTime() != null ?
						participant.getJoinTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
				participantDTO.setLeftAt(participant.getLeaveTime() != null ?
						participant.getLeaveTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
				participantDTOs.add(participantDTO);
			}
		}
		dto.setParticipants(participantDTOs);

		return dto;
	}
}