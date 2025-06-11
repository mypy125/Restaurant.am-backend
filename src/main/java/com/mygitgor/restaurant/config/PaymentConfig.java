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
    private String stripeApiKey="pk_test_51PLoRORv4Iso1jMzem1wp8wTkEdzMdrirTxOeFUpwXDYpOjdH6wYOYS5pcI7hRCZkC83xDJ6zyvALuc3mxbqVPti00Gc3fHV4G";
    private String idramApiKey;
    private String idramApiUrl;
    private String easypayApiUrl;
    private String easypayApiKey;

}
