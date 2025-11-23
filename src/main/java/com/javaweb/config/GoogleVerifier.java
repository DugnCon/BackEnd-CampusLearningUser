package com.javaweb.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleVerifier {

    private static String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    public void setClientId(String clientId) {
        CLIENT_ID = clientId;
    }

    public static GoogleIdTokenVerifier getVerifier() {
        return new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }
}

