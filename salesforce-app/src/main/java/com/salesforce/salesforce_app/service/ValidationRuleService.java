package com.salesforce.salesforce_app.service;


import com.salesforce.salesforce_app.dto.ValidationRuleDTO;
import com.salesforce.salesforce_app.model.ValidationRuleLog;
import com.salesforce.salesforce_app.repository.UserRepository;
import com.salesforce.salesforce_app.repository.ValidationRuleLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidationRuleService {

    private final ValidationRuleLogRepository
            logRepository;
    private final UserRepository userRepository;
    private final WebClient webClient =
            WebClient.create();

    // Get all validation rules
    public List<ValidationRuleDTO>
    getValidationRules(
            String token,
            String instanceUrl,
            String apiVersion) {

        String query =
                "SELECT+Id,ValidationName," +
                        "Description,Active," +
                        "ErrorMessage," +
                        "ErrorConditionFormula," +
                        "EntityDefinitionId+" +
                        "FROM+ValidationRule+" +
                        "WHERE+EntityDefinition" +
                        ".QualifiedApiName='Account'";

        String url = instanceUrl
                + "/services/data/"
                + apiVersion
                + "/tooling/query?q="
                + query;

        JsonNode response = webClient.get()
                .uri(url)
                .header("Authorization",
                        "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<ValidationRuleDTO> rules =
                new ArrayList<>();

        if (response != null
                && response.has("records")) {
            for (JsonNode record
                    : response.get("records")) {
                ValidationRuleDTO dto =
                        new ValidationRuleDTO();
                dto.setId(
                        record.get("Id").asText());
                dto.setFullName(
                        record.get("ValidationName")
                                .asText());
                dto.setActive(
                        record.get("Active")
                                .asBoolean());
                dto.setErrorMessage(
                        record.get("ErrorMessage")
                                .asText());
                dto.setDescription(
                        record.has("Description")
                                && !record.get("Description")
                                .isNull()
                                ? record.get("Description")
                                .asText() : "");
                rules.add(dto);
            }
        }
        return rules;
    }

    // Toggle single rule
    public String toggleRule(
            String token,
            String instanceUrl,
            String apiVersion,
            String ruleId,
            boolean active,
            Long userId,
            String ruleName) {

        String url = instanceUrl
                + "/services/data/"
                + apiVersion
                + "/tooling/sobjects"
                + "/ValidationRule/"
                + ruleId;

        String body =
                "{\"Metadata\":"
                        + "{\"active\":" + active + "}}";

        webClient.patch()
                .uri(url)
                .header("Authorization",
                        "Bearer " + token)
                .header("Content-Type",
                        "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Save log to database
        saveLog(userId, ruleId,
                ruleName, active);

        return active
                ? "Rule Activated ✅"
                : "Rule Deactivated ❌";
    }

    // Toggle ALL rules
    public String toggleAllRules(
            String token,
            String instanceUrl,
            String apiVersion,
            boolean active,
            Long userId) {

        List<ValidationRuleDTO> rules =
                getValidationRules(
                        token, instanceUrl, apiVersion);

        for (ValidationRuleDTO rule : rules) {
            toggleRule(token, instanceUrl,
                    apiVersion, rule.getId(),
                    active, userId,
                    rule.getFullName());
        }

        return "All rules "
                + (active ? "Activated ✅"
                : "Deactivated ❌");
    }

    // Save action log to MySQL
    private void saveLog(
            Long userId,
            String ruleId,
            String ruleName,
            boolean active) {

        userRepository.findById(userId)
                .ifPresent(user -> {
                    ValidationRuleLog log =
                            new ValidationRuleLog();
                    log.setUser(user);
                    log.setRuleId(ruleId);
                    log.setRuleName(ruleName);
                    log.setAction(active
                            ? "ACTIVATED"
                            : "DEACTIVATED");
                    logRepository.save(log);
                });
    }
}
