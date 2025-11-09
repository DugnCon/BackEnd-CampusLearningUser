package com.javaweb.model.dto.PasskeyCredential;

public class CredentialResponse {
    private String id;
    private String rawId;
    private String type;
    private CredentialResponseBody response;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRawId() {
		return rawId;
	}
	public void setRawId(String rawId) {
		this.rawId = rawId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public CredentialResponseBody getResponse() {
		return response;
	}
	public void setResponse(CredentialResponseBody response) {
		this.response = response;
	}
    
}
