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

    private static final String CLIENT_ID = "687543650693-istlhoe28vq9adl28v5lc9ojkhgo47mj.apps.googleusercontent.com";

    public static GoogleIdTokenVerifier getVerifier() {
        return new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }
}

