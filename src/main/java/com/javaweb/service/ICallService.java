package com.javaweb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.javaweb.model.dto.ChatAndCall.CallDTO;

@Service
public interface ICallService {
	List<CallDTO> getActiveCalls();
}
