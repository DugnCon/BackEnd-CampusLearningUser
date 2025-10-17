package com.javaweb.service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import org.springframework.stereotype.Service;
import com.javaweb.config.GoogleVerifier;
@Service
public class GoogleAuthService {

    public GoogleIdToken.Payload verifyGoogleToken(String idTokenString) throws Exception {
        GoogleIdToken idToken = GoogleVerifier.getVerifier().verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        }
        return null;
    }
}
