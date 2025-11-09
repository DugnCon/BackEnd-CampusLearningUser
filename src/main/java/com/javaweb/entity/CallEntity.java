package com.javaweb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="calls")
public class CallEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long CallID;
	@Column(name="Type")
	private String type;
	@Column(name="Status")
	private String status;
	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "InitiatorID")  // khóa ngoại tham chiếu sang User
	 private UserEntity users;
	public Long getCallID() {
		return CallID;
	}
	public void setCallID(Long callID) {
		CallID = callID;
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
	public UserEntity getUsers() {
		return users;
	}
	public void setUsers(UserEntity users) {
		this.users = users;
	}
	 
}
