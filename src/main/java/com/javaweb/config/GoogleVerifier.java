package com.javaweb.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

public class GoogleVerifier {

    private static final String CLIENT_ID = "687543650693-istlhoe28vq9adl28v5lc9ojkhgo47mj.apps.googleusercontent.com";

    private static final GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

    public static GoogleIdTokenVerifier getVerifier() {
        return verifier;
    }
}
