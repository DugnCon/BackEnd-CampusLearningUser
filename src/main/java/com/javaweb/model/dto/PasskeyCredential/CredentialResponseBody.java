package com.javaweb.model.dto.PasskeyCredential;

public class CredentialResponseBody {
    private String clientDataJSON;
    private String authenticatorData;
    private String signature;
    private String userHandle;
	public String getClientDataJSON() {
		return clientDataJSON;
	}
	public void setClientDataJSON(String clientDataJSON) {
		this.clientDataJSON = clientDataJSON;
	}
	public String getAuthenticatorData() {
		return authenticatorData;
	}
	public void setAuthenticatorData(String authenticatorData) {
		this.authenticatorData = authenticatorData;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getUserHandle() {
		return userHandle;
	}
	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

    
}
