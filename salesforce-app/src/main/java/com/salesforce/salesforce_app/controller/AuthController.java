package com.salesforce.salesforce_app.controller;


import com.salesforce.salesforce_app.dto.TokenResponse;
import com.salesforce.salesforce_app.model.User;
import com.salesforce.salesforce_app.service.SalesforceAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SalesforceAuthService
            authService;

    // Get Salesforce login URL
    @GetMapping("/salesforce")
    public ResponseEntity<Map<String, String>>
    getAuthUrl() {
        String url = authService.buildAuthUrl();
        return ResponseEntity.ok(
                Map.of("authUrl", url));
    }

    // Handle OAuth callback
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>>
    handleCallback(
            @RequestParam String code,
            HttpSession session) {

        TokenResponse token =
                authService.exchangeCodeForToken(
                        code);

        User user = authService.saveUser(token);

        authService.saveToSession(
                session, token, user.getId());

        // Redirect to frontend dashboard
        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "instanceUrl",
                        token.getInstanceUrl()
                ));
    }

    // Check login status
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>>
    checkStatus(HttpSession session) {
        String token =
                authService.getAccessToken(session);
        return ResponseEntity.ok(
                Map.of("loggedIn", token != null));
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>>
    logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(
                Map.of("message",
                        "Logged out successfully"));
    }
}
