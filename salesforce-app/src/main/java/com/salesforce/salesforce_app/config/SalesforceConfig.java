package com.salesforce.salesforce_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class SalesforceConfig {

    @Value("${salesforce.client-id}")
    private String clientId;

    @Value("${salesforce.client-secret}")
    private String clientSecret;

    @Value("${salesforce.redirect-uri}")
    private String redirectUri;

    @Value("${salesforce.auth-url}")
    private String authUrl;

    @Value("${salesforce.token-url}")
    private String tokenUrl;

    @Value("${salesforce.api-version}")
    private String apiVersion;
}
