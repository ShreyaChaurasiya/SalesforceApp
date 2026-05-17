package com.salesforce.salesforce_app.dto;

import lombok.Data;

@Data
public class ValidationRuleDTO {
    private String id;
    private String fullName;
    private String description;
    private boolean active;
    private String errorMessage;
    private String errorConditionFormula;
}
