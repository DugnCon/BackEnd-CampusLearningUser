package com.javaweb.model.dto.ChatAndCall;

import com.javaweb.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallDTO {
    private Long callID;
    private String type;
    private String status;
    private UserEntity userEntity;
	public Long getCallID() {
		return callID;
	}
	public void setCallID(Long callID) {
		this.callID = callID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public UserEntity getUserEntity() {
		return userEntity;
	}
	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	};
    
}