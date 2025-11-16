package com.javaweb.service;

import java.util.List;

import com.javaweb.model.dto.ChatAndCall.InitiateCallRequest;
import org.springframework.stereotype.Service;

import com.javaweb.model.dto.ChatAndCall.CallDTO;

@Service
public interface ICallService {
	/**
	 * Lấy danh sách cuộc gọi đang active
	 */
	List<CallDTO> getActiveCalls();
	/**
	 * Khởi tạo cuộc gọi mới
	 */
	CallDTO initiateCall(InitiateCallRequest request, Long userId);

	/**
	 * Trả lời cuộc gọi
	 */
	CallDTO answerCall(Long callId, Long userId);

	/**
	 * Kết thúc cuộc gọi
	 */
	CallDTO endCall(Long callId, String reason, Long userId);

	/**
	 * Từ chối cuộc gọi
	 */
	CallDTO rejectCall(Long callId, Long userId);

	/**
	 * Lấy thông tin cuộc gọi theo ID
	 */
	CallDTO getCallById(Long callId, Long userId);
}
