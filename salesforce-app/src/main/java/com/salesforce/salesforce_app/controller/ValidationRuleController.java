package com.salesforce.salesforce_app.controller;


import com.salesforce.salesforce_app.config.SalesforceConfig;
import com.salesforce.salesforce_app.dto.ValidationRuleDTO;
import com.salesforce.salesforce_app.service.SalesforceAuthService;
import com.salesforce.salesforce_app.service.ValidationRuleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class ValidationRuleController {

    private final ValidationRuleService
            ruleService;
    private final SalesforceAuthService
            authService;
    private final SalesforceConfig config;

    // GET all rules
    @GetMapping
    public ResponseEntity<
            List<ValidationRuleDTO>>
    getAllRules(HttpSession session) {

        String token =
                authService.getAccessToken(session);
        String instanceUrl =
                authService.getInstanceUrl(session);

        List<ValidationRuleDTO> rules =
                ruleService.getValidationRules(
                        token, instanceUrl,
                        config.getApiVersion());

        return ResponseEntity.ok(rules);
    }

    // PUT toggle single rule
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>>
    toggleRule(
            @PathVariable String id,
            @RequestBody
            Map<String, Object> body,
            HttpSession session) {

        String token =
                authService.getAccessToken(session);
        String instanceUrl =
                authService.getInstanceUrl(session);
        Long userId =
                authService.getUserId(session);
        boolean active =
                (Boolean) body.get("active");
        String ruleName =
                (String) body.get("ruleName");

        String result = ruleService.toggleRule(
                token, instanceUrl,
                config.getApiVersion(),
                id, active, userId, ruleName);

        return ResponseEntity.ok(
                Map.of("message", result));
    }

    // PUT toggle ALL rules
    @PutMapping("/toggle-all")
    public ResponseEntity<Map<String, String>>
    toggleAll(
            @RequestBody
            Map<String, Boolean> body,
            HttpSession session) {

        String token =
                authService.getAccessToken(session);
        String instanceUrl =
                authService.getInstanceUrl(session);
        Long userId =
                authService.getUserId(session);
        boolean active = body.get("active");

        String result =
                ruleService.toggleAllRules(
                        token, instanceUrl,
                        config.getApiVersion(),
                        active, userId);

        return ResponseEntity.ok(
                Map.of("message", result));
    }
}
