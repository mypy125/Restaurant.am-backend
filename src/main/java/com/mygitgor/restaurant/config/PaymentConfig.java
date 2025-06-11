package com.mygitgor.restaurant.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
//@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {
    private final String stripeApiSKey="sk_test_51RYlRDFLKIElximnd31WDpwdnZGJXilpkHGZOIURPKsqaF4u4C3CRIzGByiNT2nd8z5L3LyghImseaixqLxzxNNi00Zf8KPtcB";
    private String idramApiKey;
    private String idramApiUrl;
    private String easypayApiUrl;
    private String easypayApiKey;

}
