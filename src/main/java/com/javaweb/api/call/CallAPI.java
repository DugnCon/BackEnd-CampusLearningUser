package com.javaweb.api.call;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javaweb.model.dto.CallDTO;
import com.javaweb.service.ICallService;

@RestController
@RequestMapping("/api/calls")
public class CallAPI {
	@Autowired
	private ICallService callService;
    @GetMapping("/active")
    public ResponseEntity<List<CallDTO>> getActiveCalls() {
        List<CallDTO> calls = callService.getActiveCalls();
        return ResponseEntity.ok(calls);
    }
}

