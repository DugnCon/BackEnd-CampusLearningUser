package com.javaweb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="passkeycredentials")
public class PasskeyCredentialsEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long PasskeyID;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private UserEntity passkey;

    @Column(nullable = false)
    private String CredentialId; // base64url

    @Column(nullable = false, columnDefinition = "TEXT")
    private String PublicKey; // base64url
    
    private Long SignatureCount;

	public Long getPasskeyID() {
		return PasskeyID;
	}

	public void setPasskeyID(Long passkeyID) {
		PasskeyID = passkeyID;
	}

	public UserEntity getPasskey() {
		return passkey;
	}

	public void setPasskey(UserEntity passkey) {
		this.passkey = passkey;
	}

	public String getCredentialId() {
		return CredentialId;
	}

	public void setCredentialId(String credentialId) {
		CredentialId = credentialId;
	}

	public String getPublicKey() {
		return PublicKey;
	}

	public void setPublicKey(String publicKey) {
		PublicKey = publicKey;
	}

	public Long getSignatureCount() {
		return SignatureCount;
	}

	public void setSignatureCount(Long signatureCount) {
		SignatureCount = signatureCount;
	}
    
    
}
