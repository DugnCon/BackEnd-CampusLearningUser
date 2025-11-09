package com.javaweb.model.dto.PasskeyCredential;

public class CredentialVerifyRequest {
    private String email;
    private CredentialResponse response;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public CredentialResponse getResponse() {
		return response;
	}
	public void setResponse(CredentialResponse response) {
		this.response = response;
	}

    
}
