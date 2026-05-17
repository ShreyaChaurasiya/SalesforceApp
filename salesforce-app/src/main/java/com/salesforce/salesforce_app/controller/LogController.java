package com.salesforce.salesforce_app.controller;


import com.salesforce.salesforce_app.model.User;
import com.salesforce.salesforce_app.model.ValidationRuleLog;
import com.salesforce.salesforce_app.repository.UserRepository;
import com.salesforce.salesforce_app.repository.ValidationRuleLogRepository;
import com.salesforce.salesforce_app.service.SalesforceAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final ValidationRuleLogRepository
            logRepository;
    private final UserRepository userRepository;
    private final SalesforceAuthService
            authService;

    @GetMapping
    public ResponseEntity<
            List<ValidationRuleLog>>
    getLogs(HttpSession session) {

        Long userId =
                authService.getUserId(session);

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        List<ValidationRuleLog> logs =
                logRepository
                        .findByUserOrderByPerformedAtDesc(
                                user);

        return ResponseEntity.ok(logs);
    }
}
