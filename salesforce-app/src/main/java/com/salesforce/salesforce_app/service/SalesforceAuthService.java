package com.salesforce.salesforce_app.service;


import com.salesforce.salesforce_app.config.SalesforceConfig;
import com.salesforce.salesforce_app.dto.TokenResponse;
import com.salesforce.salesforce_app.model.User;
import com.salesforce.salesforce_app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesforceAuthService {

    private final SalesforceConfig config;
    private final UserRepository userRepository;
    private final WebClient webClient =
            WebClient.create();

    // Build Salesforce login URL
    public String buildAuthUrl() {
        return config.getAuthUrl()
                + "?response_type=code"
                + "&client_id=" + config.getClientId()
                + "&redirect_uri="
                + config.getRedirectUri()
                + "&scope=api+refresh_token"
                + "+offline_access";
    }

    // Exchange code for token
    public TokenResponse exchangeCodeForToken(
            String code) {
        return webClient.post()
                .uri(config.getTokenUrl())
                .body(BodyInserters
                        .fromFormData(
                                "grant_type",
                                "authorization_code")
                        .with("client_id",
                                config.getClientId())
                        .with("client_secret",
                                config.getClientSecret())
                        .with("redirect_uri",
                                config.getRedirectUri())
                        .with("code", code))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }

    // Save/update user in database
    public User saveUser(
            TokenResponse token) {
        String sfUserId = extractUserId(
                token.getId());

        Optional<User> existing =
                userRepository.findBySfUserId(
                        sfUserId);

        User user = existing.orElse(new User());
        user.setSfUserId(sfUserId);
        user.setInstanceUrl(
                token.getInstanceUrl());
        user.setAccessToken(
                token.getAccessToken());

        return userRepository.save(user);
    }

    // Save token in session
    public void saveToSession(
            HttpSession session,
            TokenResponse token,
            Long userId) {
        session.setAttribute(
                "access_token",
                token.getAccessToken());
        session.setAttribute(
                "instance_url",
                token.getInstanceUrl());
        session.setAttribute(
                "user_id", userId);
    }

    // Getters from session
    public String getAccessToken(
            HttpSession session) {
        return (String) session
                .getAttribute("access_token");
    }

    public String getInstanceUrl(
            HttpSession session) {
        return (String) session
                .getAttribute("instance_url");
    }

    public Long getUserId(
            HttpSession session) {
        return (Long) session
                .getAttribute("user_id");
    }

    // Extract SF user ID from URL
    private String extractUserId(String idUrl) {
        if (idUrl == null) return "unknown";
        String[] parts = idUrl.split("/");
        return parts[parts.length - 1];
    }
}
