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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

	@Override
	public CallDTO initiateCall(InitiateCallRequest request, Long userId) {
		try {
			log.info("INITIATE CALL - conversationId: {}, type: {}, userId: {}",
					request.getConversationID(), request.getType(), userId);

			System.out.println(request.getConversationID() + "  " + request.getType());

			// Validate request
			if (request.getConversationID() == null || request.getType() == null) {
				throw new IllegalArgumentException("Thiếu conversationId hoặc type");
			}

			if (!request.getType().equals("audio") && !request.getType().equals("video")) {
				throw new IllegalArgumentException("Type phải là 'audio' hoặc 'video'");
			}

			// Tìm conversation
			ConversationEntity conversation = conversationRepository.findById(request.getConversationID())
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy conversation với ID: " + request.getConversationID()));

			// Tìm initiator
			UserEntity initiator = userRepository.findById(userId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user initiator"));

			// Tạo Call Entity
			CallEntity callEntity = new CallEntity();
			callEntity.setConversation(conversation);
			callEntity.setInitiator(initiator);
			callEntity.setType(request.getType());
			callEntity.setStatus("initiated");
			callEntity.setStartTime(LocalDateTime.now());

			// Lưu call
			CallEntity savedCall = callRepository.save(callEntity);
			log.info("CALL ENTITY CREATED - callId: {}", savedCall.getCallID());

			// Tạo participants
			createCallParticipants(savedCall, conversation, initiator);

			// Convert to DTO và return
			CallDTO callDTO = convertToDTO(savedCall);

			log.info("INITIATE CALL SUCCESS - callId: {}, participants: {}",
					savedCall.getCallID(), callDTO.getParticipants().size());

			return callDTO;

		} catch (Exception e) {
			log.error("INITIATE CALL ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể khởi tạo cuộc gọi: " + e.getMessage());
		}
	}

	@Override
	public CallDTO answerCall(Long callId, Long userId) {
		try {
			log.info("ANSWER CALL - callId: {}, userId: {}", callId, userId);

			// Tìm call
			CallEntity call = callRepository.findById(callId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc gọi với ID: " + callId));

			// Cập nhật trạng thái call
			call.markAsActive();

			// Cập nhật participant status (người trả lời)
			updateParticipantStatus(call.getCallID(), userId, "joined");

			CallEntity updatedCall = callRepository.save(call);
			CallDTO callDTO = convertToDTO(updatedCall);

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

			// Tìm call
			CallEntity call = callRepository.findById(callId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc gọi với ID: " + callId));

			// Kết thúc call
			call.endCall();

			// Cập nhật tất cả participants status
			updateAllParticipantsStatus(call.getCallID(), "left");

			CallEntity endedCall = callRepository.save(call);
			CallDTO callDTO = convertToDTO(endedCall);

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

			// Tìm call
			CallEntity call = callRepository.findById(callId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc gọi với ID: " + callId));

			// Đánh dấu là rejected
			call.markAsRejected();

			// Cập nhật participant status (người từ chối) - SỬA: 'rejected' -> 'declined'
			updateParticipantStatus(call.getCallID(), userId, "declined");

			CallEntity rejectedCall = callRepository.save(call);
			CallDTO callDTO = convertToDTO(rejectedCall);

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

	/**
	 * Tạo participants cho call
	 */
	private void createCallParticipants(CallEntity call, ConversationEntity conversation, UserEntity initiator) {
		try {
			List<CallParticipantEntity> participants = new ArrayList<>();

			// Thêm initiator
			CallParticipantEntity initiatorParticipant = new CallParticipantEntity();
			initiatorParticipant.setCall(call);
			initiatorParticipant.setUser(initiator);
			initiatorParticipant.setJoinTime(new Date());
			initiatorParticipant.setStatus("joined"); // ✅ ĐÚNG constraint
			participants.add(initiatorParticipant);

			// Tìm user khác - SỬA: 'calling' -> 'invited'
			List<UserEntity> allUsers = userRepository.findAll();
			Optional<UserEntity> otherUserOpt = allUsers.stream()
					.filter(u -> !u.getUserID().equals(initiator.getUserID())) // Sửa field thực tế
					.findFirst();

			if (otherUserOpt.isPresent()) {
				UserEntity otherUser = otherUserOpt.get();
				CallParticipantEntity otherParticipant = new CallParticipantEntity();
				otherParticipant.setCall(call);
				otherParticipant.setUser(otherUser);
				otherParticipant.setStatus("invited"); // ✅ ĐÚNG constraint
				participants.add(otherParticipant);
			}

			// Lưu participants
			callParticipantRepository.saveAll(participants);
			call.setCallParticipant(participants);

			log.info("CREATED PARTICIPANTS - callId: {}, count: {}", call.getCallID(), participants.size());

		} catch (Exception e) {
			log.error("CREATE PARTICIPANTS ERROR: {}", e.getMessage(), e);
			throw new RuntimeException("Không thể tạo participants cho cuộc gọi: " + e.getMessage());
		}
	}

	/**
	 * Cập nhật status của participant
	 */
	private void updateParticipantStatus(Long callId, Long userId, String status) {
		try {
			Optional<CallParticipantEntity> participantOpt = callParticipantRepository
					.findByCallAndUser(callId, userId);

			if (participantOpt.isPresent()) {
				CallParticipantEntity participant = participantOpt.get();

				// Map status cho khớp constraint
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

	/**
	 * Cập nhật status của tất cả participants
	 */
	private void updateAllParticipantsStatus(Long callId, String status) {
		try {
			List<CallParticipantEntity> participants = callParticipantRepository.findByCallId(callId);

			for (CallParticipantEntity participant : participants) {
				if (!"left".equals(participant.getStatus()) && !"declined".equals(participant.getStatus())) {
					// Map status cho khớp constraint
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

	/**
	 * Map status values cho khớp với database constraint
	 * Allowed: 'declined', 'left', 'joined', 'invited'
	 */
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

	/**
	 * Convert Entity to DTO
	 */
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

		// Convert participants
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