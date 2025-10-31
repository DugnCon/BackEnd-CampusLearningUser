package com.javaweb.config;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PayPalConfig {

    // Dùng thông tin bạn cung cấp
    private static final String CLIENT_ID = "AWwRq7yNKOWYuCtZNC3IjA5F227pRhH8k5oz31jJQGw9HiID5kIwvm-eeep7TslRiJ8Q_aWQNsJ1AueO";
    private static final String CLIENT_SECRET = "EDIK36KtkVIPmVIL8y3d6qj6naXgTwrZn6ue6AWf8iHVrAbLwJBwaWaJZzk7hPYQTUDxuSTBVskVyJZm";
    private static final String MODE = "sandbox"; // test mode

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", MODE);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(CLIENT_ID, CLIENT_SECRET, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }
}

