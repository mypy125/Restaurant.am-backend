package com.mygitgor.restaurant.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {
    private StripeConfig stripe;
    private IdramConfig idram;
    private EasypayConfig easypay;

    @Getter
    @Setter
    public static class StripeConfig {
        private String secretKey;
        private String publishableKey;
    }

    @Getter
    @Setter
    public static class IdramConfig {
        private String apiUrl;
        private String apiKey;
    }

    @Getter
    @Setter
    public static class EasypayConfig {
        private String apiUrl;
        private String apiKey;
    }
}
